package com.universityweb.price.request;

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
public class PriceRequest {
    private Long id;
    private Long courseId;
    private BigDecimal price;
    private BigDecimal salePrice;
    private LocalDate startDate;
    private LocalDate  endDate;
    private Boolean isActive;

}
