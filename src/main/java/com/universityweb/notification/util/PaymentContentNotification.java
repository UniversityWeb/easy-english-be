package com.universityweb.notification.util;

public class PaymentContentNotification {
    public static String paymentSuccess(String username, String orderId, String transactionId, String amount) {
        return String.format("Hello %s! Your payment of %s for the order '%s' was successful. Transaction ID: %s.",
                username, amount, orderId, transactionId);
    }

    public static String paymentFailed(String username, String orderId, String transactionId) {
        return String.format("Dear %s, your payment for the order '%s' has failed. Please check your payment details and try again. Transaction ID: %s.",
                username, orderId, transactionId);
    }
}