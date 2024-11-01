package com.universityweb.price.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PriceResponse {
    private Long id;
    private BigDecimal price;
    private BigDecimal salePrice;
    private LocalDate  startDate;
    private LocalDate endDate;
    private Boolean isActive;
}
