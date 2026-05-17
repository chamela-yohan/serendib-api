package com.serendib.api.exception;

// Thrown when business rules are violated
// Like: email already exists, budget too low, etc.
// 400 Bad Request
public class BusinessException extends RuntimeException{
    public BusinessException(String message) {
        super(message);
    }
}
