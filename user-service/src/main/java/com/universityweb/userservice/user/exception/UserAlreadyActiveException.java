package com.universityweb.userservice.user.exception;

public class UserAlreadyActiveException extends RuntimeException {
    public UserAlreadyActiveException(String message) {
        super(message);
    }
}
