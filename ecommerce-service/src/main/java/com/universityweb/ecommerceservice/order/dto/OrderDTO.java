package com.universityweb.ecommerceservice.order.dto;

import com.universityweb.common.customenum.ECurrency;
import com.universityweb.order.entity.Order;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDTO implements Serializable {
    Long id;
    BigDecimal totalAmount;
    ECurrency currency;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Order.EStatus status;
    String username;
    List<OrderItemDTO> items;
    String previewImgPath;
}
