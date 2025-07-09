package com.pragma.plazoleta.infrastructure.adapter.output.validator;

import com.pragma.plazoleta.domain.model.RoleName;
import com.pragma.plazoleta.domain.port.output.OwnerValidatorPort;
import com.pragma.plazoleta.domain.port.output.UserClientPort;
import com.pragma.plazoleta.infrastructure.adapter.output.repository.JpaRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnerValidatorAdapter implements OwnerValidatorPort {

    private final UserClientPort userClientPort;
    private final JpaRestaurantRepository jpaRestaurantRepository;

    @Override
    public boolean isOwner(Long userId) {
        return userClientPort.getUserById(userId).getRole().getName().equals(RoleName.OWNER);
    }

    @Override
    public boolean isOwnerOfRestaurant(Long ownerId, Long restaurantId) {
        return jpaRestaurantRepository.existsByIdAndOwnerId(restaurantId, ownerId);
    }
}