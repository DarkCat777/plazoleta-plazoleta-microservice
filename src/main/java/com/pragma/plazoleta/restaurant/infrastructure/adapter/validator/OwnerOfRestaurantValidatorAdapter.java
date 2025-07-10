package com.pragma.plazoleta.restaurant.infrastructure.adapter.validator;

import com.pragma.plazoleta.dish.domain.port.output.OwnerOfRestaurantValidatorPort;
import com.pragma.plazoleta.restaurant.infrastructure.adapter.output.repository.JpaRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnerOfRestaurantValidatorAdapter implements OwnerOfRestaurantValidatorPort {

    private final JpaRestaurantRepository jpaRestaurantRepository;

    @Override
    public boolean isOwnerOfRestaurant(Long ownerId, Long restaurantId) {
        return jpaRestaurantRepository.existsByIdAndOwnerId(restaurantId, ownerId);
    }
}
