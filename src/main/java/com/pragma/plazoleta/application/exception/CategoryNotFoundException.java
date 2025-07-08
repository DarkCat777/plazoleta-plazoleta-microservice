package com.pragma.plazoleta.application.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long categoryId) {
        super("No existe la categoria con el ID: " + categoryId);
    }
}
