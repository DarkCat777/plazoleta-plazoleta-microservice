package com.pragma.plazoleta.restaurant.application.service;

import com.pragma.plazoleta.restaurant.application.dto.CreateRestaurantCommand;
import com.pragma.plazoleta.restaurant.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.restaurant.application.port.input.CreateRestaurantUseCase;
import com.pragma.plazoleta.restaurant.domain.model.Restaurant;
import com.pragma.plazoleta.restaurant.domain.port.output.UserRoleValidatorPort;
import com.pragma.plazoleta.restaurant.domain.port.output.RestaurantRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateRestaurantUseCaseImpl implements CreateRestaurantUseCase {

    private final RestaurantRepositoryPort restaurantRepositoryPort;
    private final UserRoleValidatorPort userRoleValidatorPort;

    @Override
    public Restaurant createRestaurant(CreateRestaurantCommand command) {
        if (!userRoleValidatorPort.hasOwnerRole(command.getOwnerId())) {
            throw new InvalidOwnerException("El usuario no tiene el rol OWNER");
        }
        Restaurant restaurant = Restaurant.builder()
                .name(command.getName())
                .address(command.getAddress())
                .phone(command.getPhone())
                .logoUrl(command.getLogoUrl())
                .nit(command.getNit())
                .ownerId(command.getOwnerId())
                .build();
        return restaurantRepositoryPort.save(restaurant);
    }
}

