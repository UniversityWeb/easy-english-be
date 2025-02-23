package com.universityweb.ecommerceservice.payment.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class PaymentUtils {
    public static Long generateTransactionNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);

        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000);
        return Long.parseLong(timestamp + randomNumber);
    }
}
