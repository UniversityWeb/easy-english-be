package com.universityweb.order.dto;

import com.universityweb.common.customenum.ECurrency;
import com.universityweb.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO implements Serializable {
    private Long id;
    private BigDecimal totalAmount;
    private ECurrency currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Order.EStatus status;
    private String username;
    private List<OrderItemDTO> items;
}
