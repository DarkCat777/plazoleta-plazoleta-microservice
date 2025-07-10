package com.pragma.plazoleta.restaurant.application.exception;

public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(Long restaurantId) {
        super("No existe el restaurante con el ID: " + restaurantId);
    }
}
