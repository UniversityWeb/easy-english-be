package com.universityweb.payment.service;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.payment.PaymentRepos;
import com.universityweb.payment.entity.Payment;
import com.universityweb.payment.request.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepos paymentRepos;

    @Autowired
    private UserService userService;

    @Override
    public String payCart(PaymentRequest paymentRequest) {
        User user = userService.loadUserByUsername(paymentRequest.username());

        Payment payment = Payment.builder()
                .status(Payment.EStatus.PENDING)
                .method(Payment.EMethod.VN_PAY)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        return "";
    }
}
