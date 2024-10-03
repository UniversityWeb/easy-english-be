package com.universityweb.cart.service;

import com.universityweb.cart.entity.Cart;
import com.universityweb.cart.response.CartItemResponse;
import com.universityweb.cart.response.CartResponse;

public interface CartService {
    CartItemResponse addItemToCart(String username, Long courseId);
    CartItemResponse updateItem(Long cartItemId);
    boolean removeItemFromCart(String username, Long cartItemId);
    void clearCart(String username);
    CartResponse getCartByUsername(String username);
    Cart getCartByCartItemId(Long cartItemId);
}
