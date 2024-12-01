package com.universityweb.payment.service;

import com.universityweb.FrontendRoutes;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentMapper paymentMapper;
    private final PaymentRepos paymentRepos;
    private final OrderService orderService;
    private final VNPayService vnPayService;
    private final EnrollmentService enrollmentService;
    private final NotificationService notificationService;

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
                String imagePreview = "";
                try {
                    imagePreview = order.getItems().get(0).getCourse().getImagePreview();
                } catch (Exception e) {
                    log.error(e.toString());
                }
                String username = order.getUser().getUsername();
                sendNotification(isPaymentSuccess, imagePreview, username, orderIdStr, transactionNoStr, totalAmount, paymentTime);

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

    @Transactional
    @Override
    public PaymentResponse simulateSuccess(Long orderId) {
        LocalDateTime paymentTime = LocalDateTime.now();
        Long transactionNo = PaymentUtils.generateTransactionNo();

        Order order = orderService.getOrderEntityById(orderId);
        Payment payment = order.getPayment();

        payment.setStatus(Payment.EStatus.SUCCESS);
        payment.setPaymentTime(paymentTime);
        payment.setTransactionNo(transactionNo);
        payment.setAmountPaid(order.getTotalAmount());
        payment.setCurrency(ECurrency.VND);

        order.setStatus(Order.EStatus.PAID);
        order.setUpdatedAt(paymentTime);

        Payment savedPayment = paymentRepos.save(payment);
        orderService.updateOrder(order);

        addEnrollmentsByOrderId(orderId);

        String imagePreview = "";
        String username = order.getUser().getUsername();
        sendNotification(true, imagePreview, username, String.valueOf(orderId),
                String.valueOf(transactionNo), order.getTotalAmount(), paymentTime);
        return paymentMapper.toDTO(savedPayment);
    }

    private void sendNotification(boolean isPaymentSuccess, String imagePreview, String username,
                                  String orderIdStr, String transactionNoStr, BigDecimal totalAmount, LocalDateTime paymentTime) {
        String msg = isPaymentSuccess
                ? PaymentContentNotification.paymentSuccess(username, orderIdStr, transactionNoStr, Utils.formatVND(totalAmount))
                : PaymentContentNotification.paymentFailed(username, orderIdStr, transactionNoStr);
        AddNotificationRequest notificationRequest = AddNotificationRequest.builder()
                .previewImage(imagePreview)
                .message(msg)
                .url(FrontendRoutes.getOrderDetailRoute(orderIdStr))
                .username(username)
                .createdDate(paymentTime)
                .build();

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
