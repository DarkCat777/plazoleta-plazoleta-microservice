package com.pragma.plazoleta.domain.exception;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(Long restaurantId) {
        super("No existe el restaurante con el ID: " + restaurantId);
    }
}
