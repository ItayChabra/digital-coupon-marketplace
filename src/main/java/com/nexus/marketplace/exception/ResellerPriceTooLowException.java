package com.nexus.marketplace.exception;

public class ResellerPriceTooLowException extends RuntimeException {
    public ResellerPriceTooLowException(String message) {
        super(message);
    }
}
