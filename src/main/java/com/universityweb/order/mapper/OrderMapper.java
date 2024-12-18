package com.universityweb.order.mapper;

import com.universityweb.common.infrastructure.BaseMapper;
import com.universityweb.order.dto.OrderDTO;
import com.universityweb.order.dto.OrderItemDTO;
import com.universityweb.order.entity.Order;
import com.universityweb.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper extends BaseMapper<Order, OrderDTO> {
    @Mapping(source = "user.username", target = "username")
    @Override
    OrderDTO toDTO(Order entity);

    @Mapping(target = "user", ignore = true)
    @Override
    Order toEntity(OrderDTO dto);

    @Mapping(source = "order.id", target = "orderId")
    OrderItemDTO toOrderItemDTO(OrderItem entity);
    List<OrderItemDTO> toOrderItemDTOs(List<OrderItem> entities);
    OrderItem toOrderItem(OrderItemDTO dto);
    List<OrderItem> toOrderItems(List<OrderItemDTO> dtos);
}
