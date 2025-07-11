package com.universityweb.cart.service;

import com.universityweb.cart.entity.Cart;
import com.universityweb.cart.response.CartItemResponse;
import com.universityweb.cart.response.CartResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    CartItemResponse addItemToCart(String username, Long courseId, Long bundleId);
    CartItemResponse updateItem(Long cartItemId);
    boolean removeItemFromCart(Long cartItemId);
    void clearCart(String username);
    CartResponse getCartByUsername(String username);
    Cart getCartEntityByUsername(String username);
    Cart getCartByCartItemId(Long cartItemId);
    Integer countItems(String username);
    BigDecimal getTotalAmountOfCart(String username);
    boolean existNotInCart(String username, Long courseId);
    List<CartItemResponse> addBundleToCart(String username, Long bundleId);
}
