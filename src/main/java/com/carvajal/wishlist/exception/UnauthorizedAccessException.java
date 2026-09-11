package com.carvajal.wishlist.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
