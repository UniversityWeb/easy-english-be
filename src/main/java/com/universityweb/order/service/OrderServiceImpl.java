package com.universityweb.order.service;

import com.universityweb.cart.entity.Cart;
import com.universityweb.cart.entity.CartItem;
import com.universityweb.cart.service.CartService;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.customenum.ECurrency;
import com.universityweb.course.entity.Course;
import com.universityweb.order.dto.OrderDTO;
import com.universityweb.order.dto.OrderItemDTO;
import com.universityweb.order.entity.Order;
import com.universityweb.order.entity.OrderItem;
import com.universityweb.order.exception.OrderItemNotFoundException;
import com.universityweb.order.exception.OrderNotFoundException;
import com.universityweb.order.mapper.OrderMapper;
import com.universityweb.order.repository.OrderItemRepos;
import com.universityweb.order.repository.OrderRepos;
import com.universityweb.order.response.TotalAmountResponse;
import com.universityweb.payment.PaymentRepos;
import com.universityweb.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final long EXPIRATION_CHECK_RATE_MS = 3_600_000; // 1 hour
    private static final OrderMapper orderMapper = OrderMapper.INSTANCE;

    private Logger log = LogManager.getLogger(OrderServiceImpl.class);

    private final OrderRepos orderRepos;
    private final OrderItemRepos orderItemRepos;
    private final PaymentRepos paymentRepos;
    private final CartService cartService;

    @Override
    public Order createOrderFromUserCart(String username) {
        Cart cart = cartService.getCartEntityByUsername(username);
        User user = cart.getUser();

        BigDecimal totalAmount = cartService.getTotalAmountOfCart(username);

        Order order = Order.builder()
                .totalAmount(totalAmount)
                .currency(ECurrency.VND)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .status(Order.EStatus.PENDING_PAYMENT)
                .user(user)
                .build();

        Order savedOrder = orderRepos.save(order);

        List<Long> cartItemIdsToRemove = cart.getItems().stream()
                .map(CartItem::getId)
                .toList();

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    Course course = cartItem.getCourse();
                    return OrderItem.builder()
                            .price(cartItem.getPrice())
                            .discountPercent(cartItem.getDiscountPercent())
                            .course(course)
                            .order(savedOrder)
                            .build();
                })
                .toList();

        cartItemIdsToRemove.forEach(cartService::removeItemFromCart);
        orderItemRepos.saveAll(orderItems);
        return savedOrder;
    }

    @Override
    public OrderDTO updateOrder(OrderDTO orderDTO) {
        Order existingOrder = getOrderEntityById(orderDTO.getId());

        existingOrder.setTotalAmount(orderDTO.getTotalAmount());
        existingOrder.setCurrency(orderDTO.getCurrency());
        existingOrder.setUpdatedAt(LocalDateTime.now());
        existingOrder.setStatus(orderDTO.getStatus());

        Order updatedOrder = orderRepos.save(existingOrder);
        return orderMapper.toOrderDTO(updatedOrder);
    }

    @Override
    public Page<OrderDTO> getOrders(String username, Order.EStatus status, Pageable pageable) {
        Page<Order> orders;
        if (status == null) {
            orders = orderRepos.findByUserUsername(username, pageable);
        } else {
            orders = orderRepos.findByUserUsernameAndStatus(username, status, pageable);
        }
        return orders.map(orderMapper::toOrderDTO);
    }

    @Override
    public Order getOrderEntityById(Long orderId) {
        return orderRepos.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = getOrderEntityById(orderId);
        return orderMapper.toOrderDTO(order);
    }

    @Override
    public OrderItemDTO getOrderItem(Long orderItemId) {
        OrderItem orderItem = getOrderItemEntityById(orderItemId);

        return orderMapper.toOrderItemDTO(orderItem);
    }

    @Override
    public Page<OrderItemDTO> getOrderItems(Long orderId, Pageable pageable) {
        Page<OrderItem> orderItems = orderItemRepos.findByOrderId(orderId, pageable);
        return orderItems.map(orderMapper::toOrderItemDTO);
    }

    @Override
    public OrderItem getOrderItemEntityById(Long orderItemId) {
        return orderItemRepos.findById(orderItemId)
                .orElseThrow(() -> new OrderItemNotFoundException("Order item not found with ID: " + orderItemId));
    }

    @Override
    public Order updateOrder(Order order) {
        return null;
    }

    @Override
    public TotalAmountResponse getTotalAmountByUsernameAndStatus(String username, String status) {
        Order.EStatus enumStatus = null;
        try {
            if (status != null) {
                enumStatus = Order.EStatus.valueOf(status.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status value: {}", status);
        }

        // Pass enumStatus (can be null) to the repository method
        BigDecimal totalAmount = orderRepos.getTotalAmountByUsernameAndStatus(username, enumStatus);
        return new TotalAmountResponse(totalAmount, ECurrency.VND);
    }

    @Override
    public List<OrderItem> getOrderItemsByCourseId(String username, Long courseId) {
        List<Order> orders = orderRepos.findByUser_Username(username);
        return orderItemRepos.findByCourseIdAndOrderIn(courseId, orders);
    }

    @Override
    public boolean isPurchasedCourse(String username, Long courseId) {
        List<OrderItem> orderItems = getOrderItemsByCourseId(username, courseId);
        return orderItems.stream()
                .anyMatch(orderItem -> {
                    Order order = orderItem.getOrder();
                    Payment payment = order != null ? order.getPayment() : null;

                    boolean isPaidOrder = order != null && order.getStatus() == Order.EStatus.PAID;
                    boolean isSuccessPayment = payment != null && payment.getStatus() == Payment.EStatus.SUCCESS;

                    return isPaidOrder && isSuccessPayment;
                });
    }

    @Scheduled(fixedRate = EXPIRATION_CHECK_RATE_MS)
    private void updateExpiredOrders() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = currentTime.minusMinutes(5);

        List<Order> expiredOrders = orderRepos
                .findAllByCreatedAtBeforeAndStatusNot(fiveMinutesAgo, Order.EStatus.EXPIRED);

        for (Order order : expiredOrders) {
            if (order.getStatus() == Order.EStatus.PENDING_PAYMENT) {
                order.setStatus(Order.EStatus.EXPIRED);
                Payment payment = order.getPayment();
                payment.setStatus(Payment.EStatus.FAILED);

                orderRepos.save(order);
                paymentRepos.save(payment);
            }
        }
    }
}
