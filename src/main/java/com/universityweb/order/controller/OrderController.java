package com.universityweb.order.controller;

import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.exception.CustomException;
import com.universityweb.common.media.MediaUtils;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.order.dto.OrderDTO;
import com.universityweb.order.dto.OrderItemDTO;
import com.universityweb.order.entity.Order;
import com.universityweb.order.response.TotalAmountResponse;
import com.universityweb.order.service.OrderService;
import com.universityweb.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders")
public class OrderController {

    private static final Logger log = LogManager.getLogger(OrderController.class);

    private final OrderService orderService;
    private final MediaService mediaService;
    private final AuthService authService;
    private final PaymentService paymentService;

    @GetMapping("/{username}")
    public ResponseEntity<Page<OrderDTO>> getOrdersForAdmin(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Fetching orders page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getOrders(username, null, pageable);
        return ResponseEntity.ok(MediaUtils.addMediaUrlsToOrders(mediaService, orders));
    }

    @GetMapping("/{username}/status/{status}")
    public ResponseEntity<Page<OrderDTO>> getOrdersByStatus(
            @PathVariable String username,
            @PathVariable Order.EStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Fetching orders with status: {}, page: {}, size: {}", status, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getOrders(username, status, pageable);
        return ResponseEntity.ok(MediaUtils.addMediaUrlsToOrders(mediaService, orders));
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<Page<OrderItemDTO>> getOrderItemsByOrderId(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Fetching items for order ID: {}, page: {}, size: {}", orderId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderItemDTO> orderItems = orderService.getOrderItems(orderId, pageable);
        return ResponseEntity.ok(MediaUtils.addMediaUrlsToOrderItems(mediaService, orderItems));
    }

    @GetMapping("/get-by-id/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(
            @PathVariable Long orderId
    ) {
        log.info("Fetching order details for order ID: {}", orderId);
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return ResponseEntity.ok(MediaUtils.addOrderMediaUrls(mediaService, orderDTO));
    }

    @GetMapping("/total-amount/{status}")
    public ResponseEntity<TotalAmountResponse> getTotalAmount(
            @PathVariable(value = "status")
            String status
    ) {
        log.info("Fetching total amount for status: {}", status);
        String username = authService.getCurrentUsername();
        TotalAmountResponse totalAmount = orderService.getTotalAmountByUsernameAndStatus(username, status);
        return ResponseEntity.ok(totalAmount);
    }

    @GetMapping("/is-purchased-course/{courseId}")
    public ResponseEntity<Boolean> isPurchasedCourse(
            @PathVariable
            Long courseId
    ) {
        String username = authService.getCurrentUsername();
        boolean isPurchased = orderService.isPurchasedCourse(username, courseId);
        return ResponseEntity.ok(isPurchased);
    }

    @PutMapping("/admin/update-status/{orderId}/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @PathVariable Order.EStatus status
    ) {
        Order order = orderService.getOrderEntityById(orderId);
        if (order.getStatus().equals(Order.EStatus.PAID)) {
            throw new CustomException("The status of the order cannot be changed because it has already been marked as PAID");
        }

        if (!status.equals(Order.EStatus.PAID)) {
            throw new CustomException("The status can only be updated to PAID");
        }

        String username = authService.getCurrentUsername();
        if (orderService.hasPurchasedItems(username, orderId)) {
            throw new CustomException("The order already has purchased");
        }

        order.setStatus(Order.EStatus.PAID);
        OrderDTO orderDTO = orderService.savedAndConvertToDTO(order);
        paymentService.makeOrderPaid(orderId);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping("/admin/get")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDTO>> getOrdersForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Fetching orders page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderDTO> orders = orderService.getOrders(null, pageable);
        return ResponseEntity.ok(MediaUtils.addMediaUrlsToOrders(mediaService, orders));
    }
}
