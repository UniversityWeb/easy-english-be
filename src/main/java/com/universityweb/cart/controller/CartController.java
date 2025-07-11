package com.universityweb.cart.controller;

import com.universityweb.cart.entity.Cart;
import com.universityweb.cart.response.CartItemResponse;
import com.universityweb.cart.response.CartResponse;
import com.universityweb.cart.service.CartService;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.media.MediaUtils;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.course.entity.Course;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseService;
import com.universityweb.order.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Carts")
public class CartController {

    private static final Logger log = LogManager.getLogger(CartController.class);

    private final AuthService authService;
    private final CartService cartService;
    private final CourseService courseService;
    private final MediaService mediaService;
    private final OrderService orderService;

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/")
    public ResponseEntity<CartResponse> getCart() {
        String username = authService.getCurrentUsername();
        log.info("Retrieving cart items for user: {}", username);
        CartResponse cart = cartService.getCartByUsername(username);
        log.info("Successfully retrieved cart items for user: {}", username);

        cart.getItems().forEach(item -> {
            Course course = courseService.getEntityById(item.getCourse().getId());
            CourseResponse courseResponse = courseService.mapCourseToResponse(course);
            CourseResponse newCourseResponse = MediaUtils.attachCourseMediaUrls(mediaService, courseResponse);
            item.setCourse(newCourseResponse);
        });

        return ResponseEntity.ok(cart);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/count-items")
    public ResponseEntity<Integer> countItems() {
        String username = authService.getCurrentUsername();
        log.info("Retrieving items count for user: {}", username);
        Integer countItems = cartService.countItems(username);
        log.info("Successfully retrieved items count for user: {}", username);
        return ResponseEntity.ok(countItems);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/add-item/{courseId}")
    public ResponseEntity<CartItemResponse> addItemToCart(
            @PathVariable Long courseId
    ) {
        String username = authService.getCurrentUsername();
        log.info("Adding course with ID: {} to cart for user: {}", courseId, username);
        CartItemResponse cartItem = cartService.addItemToCart(username, courseId, null);
        log.info("Successfully added course with ID: {} to cart for user: {}", courseId, username);
        return ResponseEntity.ok(cartItem);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/add-bundle/{bundleId}")
    public ResponseEntity<List<CartItemResponse>> addBundleToCart(
            @PathVariable Long bundleId
    ) {
        String username = authService.getCurrentUsername();
        log.info("Adding a bundle with ID: {} to cart for user: {}", bundleId, username);
        List<CartItemResponse> cartItem = cartService.addBundleToCart(username, bundleId);
        log.info("Successfully added bundle with ID: {} to cart for user: {}", bundleId, username);
        return ResponseEntity.ok(cartItem);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/update-item/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateItem(
            @PathVariable Long cartItemId
    ) {
        log.info("Received request to update item with ID: {}", cartItemId);

        checkAuthorization(cartItemId);
        log.info("Authorization check passed for cart item ID: {}", cartItemId);

        CartItemResponse cartItem = cartService.updateItem(cartItemId);
        log.info("Successfully updated item with ID: {}", cartItemId);

        return ResponseEntity.ok(cartItem);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/remove-item/{cartItemId}")
    public ResponseEntity<String> removeItemFromCart(
            @PathVariable Long cartItemId
    ) {
        log.info("Received request to remove item with ID: {}", cartItemId);
        checkAuthorization(cartItemId);
        String username = authService.getCurrentUsername();
        log.debug("User '{}' is attempting to remove item with ID: {}", username, cartItemId);
        cartService.removeItemFromCart(cartItemId);
        log.info("Item with ID: {} removed from cart by user: {}", cartItemId, username);
        return ResponseEntity.ok("Course removed from cart.");
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/clear")
    public ResponseEntity<String> clearCart() {
        String username = authService.getCurrentUsername();
        log.info("Clearing cart for user: {}", username);
        cartService.clearCart(username);
        log.info("Successfully cleared cart for user: {}", username);
        return ResponseEntity.ok("Cart cleared.");
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/can-add-to-cart/{courseId}")
    public ResponseEntity<Boolean> canAddToCart(@PathVariable Long courseId) {
        String username = authService.getCurrentUsername();
        boolean existNotInCart = cartService.existNotInCart(username, courseId);
        boolean isPurchasedCourse = orderService.isPurchasedCourse(username, courseId);
        return ResponseEntity.ok(existNotInCart && !isPurchasedCourse);
    }

    private void checkAuthorization(Long cartItemId) {
        Cart cart = cartService.getCartByCartItemId(cartItemId);
        String targetUsername = cart.getUser().getUsername();
        authService.checkAuthorization(targetUsername);
    }
}
