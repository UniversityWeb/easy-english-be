package com.universityweb.price.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceResponse {
    private Long id;
    private BigDecimal price;
    private BigDecimal salePrice;
    private LocalDate  startDate;
    private LocalDate endDate;
    private Boolean isActive;
}
