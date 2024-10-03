package com.universityweb.payment.service;

import com.universityweb.payment.request.PaymentRequest;

public interface PaymentService {
    String payCart(PaymentRequest paymentRequest);
}
