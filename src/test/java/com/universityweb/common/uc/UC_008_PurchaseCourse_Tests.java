package com.universityweb.common.uc;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.customenum.ECurrency;
import com.universityweb.course.entity.Course;
import com.universityweb.enrollment.service.EnrollmentService;
import com.universityweb.notification.service.NotificationService;
import com.universityweb.order.entity.OrderItem;
import com.universityweb.payment.PaymentRepos;
import com.universityweb.payment.entity.Payment;
import com.universityweb.payment.mapper.PaymentMapper;
import com.universityweb.payment.request.PaymentRequest;
import com.universityweb.payment.response.PaymentResponse;
import com.universityweb.payment.service.PaymentServiceImpl;
import com.universityweb.order.entity.Order;
import com.universityweb.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UC_008_PurchaseCourse_Tests {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentRepos paymentRepos;

    @Mock
    private OrderService orderService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePayment_SuccessWithoutPayment_CART_MANAGEMENT_POS_001() {
        // Arrange
        String username = "john_doe";
        PaymentRequest paymentRequest = new PaymentRequest(username, Payment.EMethod.NONE, null);

        Course mockCourse = Course.builder()
                .imagePreview("mockImageUrl")
                .build();

        OrderItem orderItem = OrderItem.builder()
                .course(mockCourse)
                .build();

        Order order = Order.builder()
                .id(1L)
                .status(Order.EStatus.FAILED)
                .totalAmount(BigDecimal.ZERO)
                .currency(ECurrency.USD)
                .items(List.of(orderItem))
                .user(User.builder().username("mockuser").build())
                .build();

        Payment payment = Payment.builder()
                .id(1L)
                .status(Payment.EStatus.SUCCESS)
                .method(Payment.EMethod.NONE)
                .paymentTime(LocalDateTime.now())
                .amountPaid(BigDecimal.ZERO)
                .currency(ECurrency.USD)
                .order(order)
                .build();

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .id(1L)
                .status(Payment.EStatus.SUCCESS)
                .method(Payment.EMethod.NONE)
                .amountPaid(BigDecimal.ZERO)
                .currency(ECurrency.USD)
                .orderId("1")
                .username(username)
                .build();

        when(orderService.createOrderFromUserCart(username)).thenReturn(order);
        when(paymentRepos.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDTO(payment)).thenReturn(paymentResponse);
        when(orderService.getOrderEntityById(order.getId())).thenReturn(order);

        // Act
        PaymentResponse result = paymentService.createPayment(paymentRequest);

        // Assert
        assertNotNull(result);
        assertEquals(Payment.EStatus.SUCCESS, result.getStatus());
        assertEquals(BigDecimal.ZERO, result.getAmountPaid());
        verify(orderService, times(1)).createOrderFromUserCart(username);
        verify(paymentRepos, times(1)).save(any(Payment.class));
        verify(paymentMapper, times(1)).toDTO(payment);
    }
}
