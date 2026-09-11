package com.carvajal.wishlist.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
