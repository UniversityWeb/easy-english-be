package com.universityweb.cart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO implements Serializable {
    private Long id;
    private CartItem.EStatus status;
    private BigDecimal price;
    private BigDecimal discountPercent;
    private LocalDateTime updatedAt;
    private String username;
    private Long courseId;
}
