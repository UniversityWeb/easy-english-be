package com.universityweb.payment.service;

import com.universityweb.common.Utils;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.customenum.ECurrency;
import com.universityweb.common.request.GetByUsernameRequest;
import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.enrollment.request.AddEnrollmentRequest;
import com.universityweb.enrollment.service.EnrollmentService;
import com.universityweb.course.entity.Course;
import com.universityweb.course.service.CourseService;
import com.universityweb.order.entity.Order;
import com.universityweb.order.entity.OrderItem;
import com.universityweb.order.service.OrderService;
import com.universityweb.payment.PaymentRepos;
import com.universityweb.payment.entity.Payment;
import com.universityweb.payment.exception.PaymentNotFoundException;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
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
    private CourseService courseService;

    @Override
    public String createPayment(PaymentRequest paymentRequest) {
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
                        String.valueOf(payment.getId()),
                        paymentRequest.urlReturn());
            }
            default -> throw new UnsupportedPaymentMethodException("Payment method " + method + " is not supported.");
        }

        return paymentUrl;
    }

    @Override
    public PaymentResponse processPaymentResult(
            HttpServletRequest req, Map<String,
            String> params
    ) {
        String methodStr = params.get("method");
        Payment.EMethod method = Payment.EMethod.valueOf(methodStr);

        switch (method) {
            case VN_PAY -> {
                String paymentIdStr = params.get("vnp_OrderInfo");
                Long paymentId = Long.parseLong(paymentIdStr);
                String paymentTimeStrInMillis = params.get("vnp_PayDate");
                LocalDateTime paymentTime = Utils.convertMillisToLocalDateTime(paymentTimeStrInMillis);
                String transactionNoStr = params.get("vnp_TransactionNo");
                Long transactionNo = Long.parseLong(transactionNoStr);
                String totalAmountStr = params.get("vnp_Amount");
                BigDecimal totalAmount = new BigDecimal(Long.parseLong(totalAmountStr) / VNPayConfig.VND_MULTIPLIER);

                Payment payment = getPaymentById(paymentId);

                int paymentStatus = vnPayService.orderReturn(req);
                payment.setStatus(paymentStatus == 1 ? Payment.EStatus.SUCCESS : Payment.EStatus.FAILED);
                payment.setPaymentTime(paymentTime);
                payment.setTransactionNo(transactionNo);
                payment.setAmountPaid(totalAmount);
                payment.setCurrency(ECurrency.VND);

                Payment savedPayment = paymentRepos.save(payment);
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
    public PaymentResponse simulateSuccess(Long orderId) {
        Order order = orderService.getOrderEntityById(orderId);

        Payment payment = order.getPayments().stream()
                .filter(p -> p.getStatus() == Payment.EStatus.PENDING)
                .max(Comparator.comparing(Payment::getPaymentTime))
                .orElseThrow(() -> new IllegalStateException("No valid payment found for order"));

        LocalDateTime paymentTime = LocalDateTime.now();
        Long transactionNo = PaymentUtils.generateTransactionNo();

        payment.setStatus(Payment.EStatus.SUCCESS);
        payment.setPaymentTime(paymentTime);
        payment.setTransactionNo(transactionNo);
        payment.setAmountPaid(order.getTotalAmount());
        payment.setCurrency(ECurrency.VND);

        Payment savedPayment = paymentRepos.save(payment);

        addEnrollmentsByOrderId(orderId);

        return paymentMapper.toDTO(savedPayment);
    }

    private Payment getPaymentById(Long paymentId) {
        String msg = "Could not find any payment with id=" + paymentId;
        return paymentRepos.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(msg));
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
                    user.getUsername(),
                    course.getId()
            );
            enrollmentService.addNewEnrollment(addEnrollmentRequest);
        }
    }
}
