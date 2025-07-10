package com.pragma.plazoleta.dish.domain.port.output;

public interface OwnerOfRestaurantValidatorPort {

    boolean isOwnerOfRestaurant(Long ownerId, Long restaurantId);

}
