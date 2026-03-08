package com.nexus.marketplace.exception;

public class ProductAlreadySoldException extends RuntimeException {
    public ProductAlreadySoldException(String message) {
        super(message);
    }
}
