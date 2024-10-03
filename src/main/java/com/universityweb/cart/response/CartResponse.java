package com.universityweb.cart.response;

import com.universityweb.cart.entity.CartItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CartResponse(
        Long id,
        BigDecimal totalAmount,
        LocalDateTime updatedAt,
        String username,
        List<CartItem> items
) {}
