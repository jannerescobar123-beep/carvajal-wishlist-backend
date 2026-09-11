package com.carvajal.wishlist.exception;

import org.springframework.http.HttpStatus;

public class ProductAlreadyInWishlistException extends RuntimeException {
    public ProductAlreadyInWishlistException(String message) {
        super(message);
    }
}
