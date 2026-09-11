package com.carvajal.wishlist.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
