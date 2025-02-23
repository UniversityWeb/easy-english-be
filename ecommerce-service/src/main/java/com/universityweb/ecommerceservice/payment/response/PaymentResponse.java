package com.universityweb.ecommerceservice.payment.response;

import com.universityweb.common.customenum.ECurrency;
import com.universityweb.payment.entity.Payment;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentResponse {
    private Long id;
    private Payment.EStatus status;
    private Payment.EMethod method;
    private LocalDateTime paymentTime;
    private Long transactionNo;
    private BigDecimal amountPaid;
    private ECurrency currency;
    private String orderId;
    private String username;
    private String paymentUrl;
}
