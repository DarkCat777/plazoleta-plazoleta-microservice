package com.pragma.plazoleta.domain.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long categoryId) {
        super("No existe la categoria con el ID: " + categoryId);
    }
}
