package com.universityweb.payment.model.request;

import com.universityweb.payment.model.Payment;

import java.util.List;

public record PaymentRequest(
        String username,
        List<Long> courseIds,
        Payment.EMethod method,
        String urlReturn
) {}
