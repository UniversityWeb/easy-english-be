package com.universityweb.payment;

import com.universityweb.payment.vnpay.VNPayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Tag(name = "Payment")
public class PaymentController {

    private static final Logger log = LogManager.getLogger(PaymentController.class);

    private final VNPayService vnPayService;

    @PostMapping("/create-payment")
    public ResponseEntity<String> createPaymentOrder(
            HttpServletRequest req,
            @RequestParam String username,
            @RequestParam int amount,
            @RequestParam String orderInfo
    ) {
        String scheme = req.getScheme();
        String serverName = req.getServerName();
        int port = req.getServerPort();
        String contextPath = req.getContextPath();
        String baseUrl = scheme + "://" + serverName + ":" + port + contextPath;

        log.info("Base URL for return: {}", baseUrl);
        String vnpayUrl = vnPayService.createOrder(amount, orderInfo, baseUrl);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vnpayUrl);
    }

    @GetMapping("/payment-return")
    public ResponseEntity<Map<String, Object>> processPaymentReturn(HttpServletRequest req) {
        Map<String, Object> response = new HashMap<>();
        int paymentStatus = vnPayService.orderReturn(req);

        String orderInfo = req.getParameter("vnp_OrderInfo");
        String paymentTime = req.getParameter("vnp_PayDate");
        String transactionId = req.getParameter("vnp_TransactionNo");
        String totalPriceStr = req.getParameter("vnp_Amount");
        int totalPrice = Integer.parseInt(totalPriceStr) / 100;


        response.put("orderId", orderInfo);
        response.put("totalPrice", totalPrice);
        response.put("paymentTime", paymentTime);
        response.put("transactionId", transactionId);
        response.put("paymentStatus", paymentStatus == 1 ? "success" : "fail");

        return ResponseEntity.ok(response);
    }
}
