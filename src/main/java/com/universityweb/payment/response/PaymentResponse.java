package com.universityweb.payment.response;

import com.universityweb.common.customenum.ECurrency;
import com.universityweb.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
