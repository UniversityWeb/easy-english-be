package com.universityweb.ecommerceservice.payment.exception;

public class UnsupportedPaymentMethodException extends RuntimeException {
    public UnsupportedPaymentMethodException(String message) {
        super(message);
    }
}
