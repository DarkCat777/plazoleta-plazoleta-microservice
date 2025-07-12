package com.pragma.plazoleta.domain.spi;

public interface OwnerValidatorPort {
    boolean isOwner(Long userId);

    boolean isOwnerOfRestaurant(Long ownerId, Long restaurantId);
}
