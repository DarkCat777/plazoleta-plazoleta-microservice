package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.CreateRestaurantCommand;
import com.pragma.plazoleta.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.application.port.input.CreateRestaurantUseCase;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.port.output.OwnerValidatorPort;
import com.pragma.plazoleta.domain.port.output.RestaurantRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateRestaurantService implements CreateRestaurantUseCase {

    private final RestaurantRepository repository;
    private final OwnerValidatorPort validator;

    @Override
    public Restaurant createRestaurant(CreateRestaurantCommand command) {
        if (!validator.isOwner(command.getOwnerId())) {
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
        return repository.save(restaurant);
    }
}

