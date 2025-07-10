package com.pragma.plazoleta.dish.application.exception;

public class DishNotFoundException extends RuntimeException {
    public DishNotFoundException(Long dishId) {
        super("No existe el plato con el ID: " + dishId);
    }
}
