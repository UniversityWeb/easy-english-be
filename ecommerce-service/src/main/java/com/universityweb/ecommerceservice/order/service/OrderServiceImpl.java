package com.universityweb.ecommerceservice.order.service;

import com.universityweb.cart.entity.Cart;
import com.universityweb.cart.entity.CartItem;
import com.universityweb.cart.service.CartService;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.customenum.ECurrency;
import com.universityweb.common.exception.CustomException;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl
        extends BaseServiceImpl<Order, OrderDTO, Long, OrderRepos, OrderMapper>
        implements OrderService {

    private static final long EXPIRATION_CHECK_RATE_MS = 3_600_000; // 1 hour
    private Logger log = LogManager.getLogger(OrderServiceImpl.class);

    private final OrderItemRepos orderItemRepos;
    private final PaymentRepos paymentRepos;
    private final CartService cartService;

    @Autowired
    public OrderServiceImpl(
            OrderRepos repository,
            OrderMapper mapper,
            OrderItemRepos orderItemRepos,
            PaymentRepos paymentRepos,
            CartService cartService
    ) {
        super(repository, mapper);
        this.orderItemRepos = orderItemRepos;
        this.paymentRepos = paymentRepos;
        this.cartService = cartService;
    }

    @Override
    public Order createOrderFromUserCart(String username) {
        Cart cart = cartService.getCartEntityByUsername(username);
        User user = cart.getUser();

        BigDecimal totalAmount = cartService.getTotalAmountOfCart(username);

        Order order = Order.builder()
                .totalAmount(totalAmount)
                .currency(ECurrency.VND)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(Order.EStatus.PENDING_PAYMENT)
                .user(user)
                .build();

        Order savedOrder = repository.save(order);

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

        return savedAndConvertToDTO(existingOrder);
    }

    @Override
    public Page<OrderDTO> getOrders(String username, Order.EStatus status, Pageable pageable) {
        Page<Order> orders;
        if (status == null) {
            orders = repository.findByUserUsername(username, pageable);
        } else {
            orders = repository.findByUserUsernameAndStatus(username, status, pageable);
        }
        return mapper.mapPageToPageDTO(orders);
    }

    @Override
    public Page<OrderDTO> getOrders(Order.EStatus status, Pageable pageable) {
        Page<Order> orders;
        if (status == null) {
            orders = repository.findAll(pageable);
        } else {
            orders = repository.findByStatus(status, pageable);
        }
        return mapper.mapPageToPageDTO(orders);
    }

    @Override
    public Order getOrderEntityById(Long orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = getOrderEntityById(orderId);
        return mapper.toDTO(order);
    }

    @Override
    public OrderItemDTO getOrderItem(Long orderItemId) {
        OrderItem orderItem = getOrderItemEntityById(orderItemId);
        return mapper.toOrderItemDTO(orderItem);
    }

    @Override
    public Page<OrderItemDTO> getOrderItems(Long orderId, Pageable pageable) {
        Page<OrderItem> orderItems = orderItemRepos.findByOrderId(orderId, pageable);
        return orderItems.map(mapper::toOrderItemDTO);
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
        BigDecimal totalAmount = repository.getTotalAmountByUsernameAndStatus(username, enumStatus);
        return new TotalAmountResponse(totalAmount, ECurrency.VND);
    }

    @Override
    public boolean isPurchasedCourse(String username, Long courseId) {
        return repository.findByUserUsernameAndStatus(username, Order.EStatus.PAID).stream()
                .flatMap(order -> order.getItems().stream())
                .anyMatch(item -> item.getCourse().getId().equals(courseId));
    }

    @Override
    public boolean hasPurchasedItems(String username, Long orderId) {
        List<Order> paidOrders = repository.findByUserUsernameAndStatus(username, Order.EStatus.PAID);
        List<Long> paidCourseIds = new ArrayList<>();
        for (Order order : paidOrders) {
            for (OrderItem orderItem : order.getItems()) {
                paidCourseIds.add(orderItem.getCourse().getId());
            }
        }
        Order order = getOrderEntityById(orderId);
        for (OrderItem orderItem : order.getItems()) {
            Long courseId = orderItem.getCourse().getId();
            if (paidCourseIds.contains(courseId)) {
                return true;
            }
        }
        return false;
    }

    @Scheduled(fixedRate = EXPIRATION_CHECK_RATE_MS)
    public void updateExpiredOrders() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = currentTime.minusMinutes(5);

        List<Order> expiredOrders = repository
                .findAllByCreatedAtBeforeAndStatusNot(fiveMinutesAgo, Order.EStatus.EXPIRED);

        for (Order order : expiredOrders) {
            if (order.getStatus() == Order.EStatus.PENDING_PAYMENT) {
                order.setStatus(Order.EStatus.EXPIRED);
                Payment payment = order.getPayment();
                payment.setStatus(Payment.EStatus.FAILED);

                repository.save(order);
                paymentRepos.save(payment);
            }
        }
    }

    @Override
    protected void throwNotFoundException(Long id) {
        throw new CustomException("Could not find order with ID: " + id);
    }
}
