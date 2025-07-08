package com.pragma.plazoleta.infrastructure.adapter.output.validator;

import com.pragma.plazoleta.domain.model.RoleName;
import com.pragma.plazoleta.domain.port.output.OwnerValidatorPort;
import com.pragma.plazoleta.infrastructure.adapter.output.client.UserClient;
import com.pragma.plazoleta.infrastructure.adapter.output.repository.JpaRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnerValidatorAdapter implements OwnerValidatorPort {

    private final UserClient userClient;
    private final JpaRestaurantRepository jpaRestaurantRepository;

    @Override
    public boolean isOwner(Long userId) {
        return userClient.getUserById(userId).getRole().getName().equals(RoleName.OWNER.name());
    }

    @Override
    public boolean isOwnerOfRestaurant(Long ownerId, Long restaurantId) {
        return jpaRestaurantRepository.existsByIdAndOwnerId(restaurantId, ownerId);
    }
}