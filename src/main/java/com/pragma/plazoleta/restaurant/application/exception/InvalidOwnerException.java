package com.pragma.plazoleta.restaurant.application.exception;

public class InvalidOwnerException extends RuntimeException {
    public InvalidOwnerException(String message) {
        super(message);
    }
}
