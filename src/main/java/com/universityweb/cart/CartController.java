package com.universityweb.cart;

import com.universityweb.cart.response.CartItemResponse;
import com.universityweb.cart.service.CartService;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart")
public class CartController {

    private static final Logger log = LogManager.getLogger(CartController.class);

    private final AuthService authService;
    private final CartService cartService;

    @Operation(
            summary = "Get Cart Items",
            description = "Retrieve all items in the user's cart.",
            responses = {
                    @ApiResponse(
                            description = "Successfully retrieved cart items.",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartItemResponse.class))
                    ),
                    @ApiResponse(
                            description = "Unauthorized: User does not have permission to view this cart.",
                            responseCode = "403",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(
                            description = "Internal server error.",
                            responseCode = "500",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @GetMapping("/{username}")
    public ResponseEntity<List<CartItemResponse>> getCartItems(@PathVariable String username) {
        log.info("Retrieving cart items for user: {}", username);
        authService.checkAuthorization(username);
        List<CartItemResponse> cartItems = cartService.getCartItems(username);
        log.info("Successfully retrieved cart items for user: {}", username);
        return ResponseEntity.ok(cartItems);
    }

    @Operation(
            summary = "Add Item to Cart",
            description = "Add a course to the user's cart.",
            responses = {
                    @ApiResponse(
                            description = "Course successfully added to cart.",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartItemResponse.class))
                    ),
                    @ApiResponse(
                            description = "Internal server error.",
                            responseCode = "500",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PostMapping("/add/{username}/{courseId}")
    public ResponseEntity<CartItemResponse> addItemToCart(
            @PathVariable String username,
            @PathVariable Long courseId
    ) {
        log.info("Adding course with ID: {} to cart for user: {}", courseId, username);
        authService.checkAuthorization(username);
        CartItemResponse cartItem = cartService.addItemToCart(username, courseId);
        log.info("Successfully added course with ID: {} to cart for user: {}", courseId, username);
        return ResponseEntity.ok(cartItem);
    }

    @Operation(
            summary = "Remove Item from Cart",
            description = "Remove a course from the user's cart.",
            responses = {
                    @ApiResponse(
                            description = "Course successfully removed from cart.",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            description = "Internal server error.",
                            responseCode = "500",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PutMapping("/remove/{username}/{courseId}")
    public ResponseEntity<String> removeItemFromCart(
            @PathVariable String username,
            @PathVariable Long courseId
    ) {
        log.info("Removing course with ID: {} from cart for user: {}", courseId, username);
        authService.checkAuthorization(username);
        cartService.removeItemFromCart(username, courseId);
        log.info("Successfully removed course with ID: {} from cart for user: {}", courseId, username);
        return ResponseEntity.ok("Course removed from cart.");
    }

    @Operation(
            summary = "Clear Cart",
            description = "Clear all items from the user's cart.",
            responses = {
                    @ApiResponse(
                            description = "Cart successfully cleared.",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            description = "Internal server error.",
                            responseCode = "500",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    @PutMapping("/clear/{username}")
    public ResponseEntity<String> clearCart(
            @PathVariable String username
    ) {
        log.info("Clearing cart for user: {}", username);
        authService.checkAuthorization(username);
        cartService.clearCart(username);
        log.info("Successfully cleared cart for user: {}", username);
        return ResponseEntity.ok("Cart cleared.");
    }
}
