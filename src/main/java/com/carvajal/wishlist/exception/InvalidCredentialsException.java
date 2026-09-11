package com.carvajal.wishlist.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
