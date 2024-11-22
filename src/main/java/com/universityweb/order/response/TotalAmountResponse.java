package com.universityweb.order.response;

import com.universityweb.common.customenum.ECurrency;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TotalAmountResponse {
    BigDecimal totalAmount;
    ECurrency currency;
}
