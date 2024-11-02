package com.universityweb.payment.service;

import com.universityweb.common.Utils;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.customenum.ECurrency;
import com.universityweb.common.request.GetByUsernameRequest;
import com.universityweb.course.entity.Course;
import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.enrollment.request.AddEnrollmentRequest;
import com.universityweb.enrollment.service.EnrollmentService;
import com.universityweb.notification.request.AddNotificationRequest;
import com.universityweb.notification.service.NotificationService;
import com.universityweb.notification.util.PaymentContentNotification;
import com.universityweb.order.entity.Order;
import com.universityweb.order.entity.OrderItem;
import com.universityweb.order.repository.OrderRepos;
import com.universityweb.order.service.OrderService;
import com.universityweb.payment.PaymentRepos;
import com.universityweb.payment.entity.Payment;
import com.universityweb.payment.exception.UnsupportedPaymentMethodException;
import com.universityweb.payment.mapper.PaymentMapper;
import com.universityweb.payment.request.GetPaymentsByUsernameAndStatusRequest;
import com.universityweb.payment.request.PaymentRequest;
import com.universityweb.payment.response.PaymentResponse;
import com.universityweb.payment.util.PaymentUtils;
import com.universityweb.payment.vnpay.VNPayConfig;
import com.universityweb.payment.vnpay.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentMapper paymentMapper = PaymentMapper.INSTANCE;

    @Autowired
    private PaymentRepos paymentRepos;

    @Autowired
    private OrderService orderService;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private OrderRepos orderRepos;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        String paymentUrl;
        String username = paymentRequest.username();
        Payment.EMethod method = paymentRequest.method();

        Order savedOrder = orderService.createOrderFromUserCart(username);

        Payment payment = Payment.builder()
                .status(Payment.EStatus.PENDING)
                .method(method)
                .paymentTime(LocalDateTime.now())
                .currency(savedOrder.getCurrency())
                .order(savedOrder)
                .build();
        paymentRepos.save(payment);

        switch (method) {
            case VN_PAY -> {
                int amount = savedOrder.getTotalAmount().intValue();
                paymentUrl = vnPayService.createOrder(
                        amount,
                        String.valueOf(savedOrder.getId()),
                        paymentRequest.urlReturn());
            }
            default -> throw new UnsupportedPaymentMethodException("Payment method " + method + " is not supported.");
        }

        PaymentResponse paymentResponse = paymentMapper.toDTO(payment);
        paymentResponse.setPaymentUrl(paymentUrl);
        return paymentResponse;
    }

    @Override
    @Transactional
    public PaymentResponse processPaymentResult(
            Payment.EMethod method,
            HttpServletRequest req,
            Map<String, String> params
    ) {
        switch (method) {
            case VN_PAY -> {
                String orderIdStr = params.get("vnp_OrderInfo");
                Long orderId = Long.parseLong(orderIdStr);
                String paymentTimeStrInMillis = params.get("vnp_PayDate");
                LocalDateTime paymentTime = Utils.convertMillisToLocalDateTime(paymentTimeStrInMillis);
                String transactionNoStr = params.get("vnp_TransactionNo");
                Long transactionNo = Long.parseLong(transactionNoStr);
                String totalAmountStr = params.get("vnp_Amount");
                BigDecimal totalAmount = new BigDecimal(Long.parseLong(totalAmountStr) / VNPayConfig.VND_MULTIPLIER);

                Order order = orderService.getOrderEntityById(orderId);
                Payment payment = order.getPayment();

                int paymentStatus = vnPayService.orderReturn(req);
                boolean isPaymentSuccess = paymentStatus == 1;
                payment.setStatus(isPaymentSuccess ? Payment.EStatus.SUCCESS : Payment.EStatus.FAILED);
                payment.setPaymentTime(paymentTime);
                payment.setTransactionNo(transactionNo);
                payment.setAmountPaid(totalAmount);
                payment.setCurrency(ECurrency.VND);

                order.setStatus(isPaymentSuccess ? Order.EStatus.PAID : Order.EStatus.FAILED);
                order.setUpdatedAt(paymentTime);

                Payment savedPayment = paymentRepos.save(payment);
                orderService.updateOrder(order);
                addEnrollmentsByOrderId(orderId);

                String username = order.getUser().getUsername();
                sendNotification(isPaymentSuccess, username, orderIdStr, transactionNoStr, totalAmountStr, paymentTime);

                return paymentMapper.toDTO(savedPayment);
            }
            default -> throw new UnsupportedPaymentMethodException("Payment method " + method + " is not supported.");
        }
    }

    @Override
    public Page<PaymentResponse> getPaymentsByUsername(GetByUsernameRequest request) {
        String username = request.getUsername();
        Pageable pageable = createPageable(request.getPage(), request.getSize());
        Page<Payment> paymentPage = paymentRepos.findByUsername(username, pageable);
        return paymentPage.map(paymentMapper::toDTO);
    }

    @Override
    public Page<PaymentResponse> getPaymentsByUsernameAndStatus(GetPaymentsByUsernameAndStatusRequest request) {
        String username = request.getUsername();
        Payment.EStatus status = request.getStatus();
        Pageable pageable = createPageable(request.getPage(), request.getSize());
        Page<Payment> paymentPage = paymentRepos.findByUsernameAndStatus(username, status, pageable);
        return paymentPage.map(paymentMapper::toDTO);
    }

    @Override
    @Transactional
    public PaymentResponse simulateSuccess(Long orderId) {
        Order order = orderService.getOrderEntityById(orderId);
        LocalDateTime paymentTime = LocalDateTime.now();
        Long transactionNo = PaymentUtils.generateTransactionNo();
        Payment payment = order.getPayment();

        order.setUpdatedAt(paymentTime);
        order.setStatus(Order.EStatus.PAID);

        payment.setStatus(Payment.EStatus.SUCCESS);
        payment.setPaymentTime(paymentTime);
        payment.setTransactionNo(transactionNo);
        payment.setAmountPaid(order.getTotalAmount());
        payment.setCurrency(ECurrency.VND);

        orderRepos.save(order);
        Payment savedPayment = paymentRepos.save(payment);

        addEnrollmentsByOrderId(orderId);

        return paymentMapper.toDTO(savedPayment);
    }

    private void sendNotification(boolean isPaymentSuccess, String username,
                                  String orderIdStr, String transactionNoStr, String totalAmountStr, LocalDateTime paymentTime) {
        String msg = isPaymentSuccess
                ? PaymentContentNotification.paymentSuccess(username, orderIdStr, transactionNoStr, totalAmountStr)
                : PaymentContentNotification.paymentFailed(username, orderIdStr, totalAmountStr);
        AddNotificationRequest notificationRequest = new AddNotificationRequest(
                msg,
                username,
                paymentTime);
        notificationService.sendRealtimeNotification(notificationRequest);
    }

    private Pageable createPageable(int page, int size) {
        int pageNumber = (page < 0) ? 0 : page;
        int pageSize = (size <= 0) ? 10 : size;
        Sort sort = Sort.by("paymentTime");
        return PageRequest.of(pageNumber, pageSize, sort.descending());
    }

    private void addEnrollmentsByOrderId(Long orderId) {
        Order order = orderService.getOrderEntityById(orderId);
        List<OrderItem> orderItems = order.getItems();

        User user = order.getUser();

        Enrollment.EType enrollmentType = order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0
                ? Enrollment.EType.PAID
                : Enrollment.EType.FREE;

        for (OrderItem orderItem : orderItems) {
            Course course = orderItem.getCourse();

            AddEnrollmentRequest addEnrollmentRequest = new AddEnrollmentRequest(
                    Enrollment.EStatus.ACTIVE,
                    enrollmentType,
                    order.getUpdatedAt(),
                    order.getUpdatedAt(),
                    user.getUsername(),
                    course.getId()
            );
            enrollmentService.addNewEnrollment(addEnrollmentRequest);
        }
    }
}
