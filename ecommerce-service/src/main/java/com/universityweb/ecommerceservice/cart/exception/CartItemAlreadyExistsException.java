package com.universityweb.ecommerceservice.cart.exception;

public class CartItemAlreadyExistsException extends RuntimeException {
    public CartItemAlreadyExistsException(String message) {
        super(message);
    }
}
