package com.universityweb.payment.service;

import com.universityweb.payment.model.request.PaymentRequest;

public interface PaymentService {
    String payCart(PaymentRequest paymentRequest);
}
