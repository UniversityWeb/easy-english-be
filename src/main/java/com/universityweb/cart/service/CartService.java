package com.universityweb.cart.service;

import com.universityweb.cart.response.CartItemResponse;
import com.universityweb.cart.response.CartResponse;

import java.util.List;

public interface CartService {
    CartResponse createCartForUser(String username);
    List<CartItemResponse> getCartItems(String username);
    CartItemResponse addItemToCart(String username, Long courseId);
    boolean removeItemFromCart(String username, Long courseId);
    void clearCart(String username);
}
