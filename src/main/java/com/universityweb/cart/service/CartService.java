package com.universityweb.cart.service;

import com.universityweb.cart.model.CartItemDTO;

import java.util.List;

public interface CartService {
    List<CartItemDTO> getCartItems(String username);
    CartItemDTO addItemToCart(String username, Long courseId);
    boolean removeItemFromCart(String username, Long courseId);
    void clearCart(String username);
}
