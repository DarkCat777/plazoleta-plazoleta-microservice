package com.pragma.plazoleta.domain.exception;

public class RestaurantNotFoundException extends RuntimeException {

    public static final String OWNER_DONT_HAVE_RESTAURANT = "El propietario no tiene un restaurante.";

    public static final String RESTAURANT_NOT_EXIST = "No existe el restaurante con el ID: ";

    public RestaurantNotFoundException(String message) {
        super(message);
    }

    public RestaurantNotFoundException(Long restaurantId) {
        super(RESTAURANT_NOT_EXIST + restaurantId);
    }
}
