package com.pragma.plazoleta.domain.exception;

public class DishNotFoundException extends RuntimeException {
    public DishNotFoundException(Long dishId) {
        super("No existe el plato con el ID: " + dishId);
    }
}
