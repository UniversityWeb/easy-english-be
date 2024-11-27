package com.universityweb.order.service;

import com.universityweb.common.infrastructure.service.BaseService;
import com.universityweb.order.dto.OrderDTO;
import com.universityweb.order.dto.OrderItemDTO;
import com.universityweb.order.entity.Order;
import com.universityweb.order.entity.OrderItem;
import com.universityweb.order.response.TotalAmountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService extends BaseService<Order, OrderDTO, Long> {
    Order createOrderFromUserCart(String username);
    OrderDTO updateOrder(OrderDTO orderDTO);
    Page<OrderDTO> getOrders(String username, Order.EStatus status, Pageable pageable);
    Page<OrderDTO> getOrders(Order.EStatus status, Pageable pageable);
    Order getOrderEntityById(Long orderId);
    OrderDTO getOrderById(Long orderId);


    OrderItemDTO getOrderItem(Long orderItemId);
    Page<OrderItemDTO> getOrderItems(Long orderItemId, Pageable pageable);
    OrderItem getOrderItemEntityById(Long orderItemId);
    Order updateOrder(Order order);

    TotalAmountResponse getTotalAmountByUsernameAndStatus(String username, String status);
    List<OrderItem> getOrderItemsByCourseId(String username, Long courseId);

    boolean isPurchasedCourse(String username, Long courseId);
}
