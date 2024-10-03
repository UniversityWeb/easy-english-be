package com.universityweb.payment.request;

import com.universityweb.payment.entity.Payment;

import java.util.List;

public record PaymentRequest(
        String username,
        List<Long> courseIds,
        Payment.EMethod method,
        String urlReturn
) {}
