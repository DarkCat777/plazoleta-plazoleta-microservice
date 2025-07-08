package com.pragma.plazoleta.domain.port.output;

public interface OwnerValidatorPort {
    boolean isOwner(Long userId);

    boolean isOwnerOfRestaurant(Long ownerId, Long restaurantId);
}
