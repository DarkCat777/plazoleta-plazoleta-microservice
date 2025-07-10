package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.CreateDishCommand;
import com.pragma.plazoleta.application.exception.CategoryNotFoundException;
import com.pragma.plazoleta.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.application.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.application.port.input.CreateDishUseCase;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.port.output.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateDishUseCaseImpl implements CreateDishUseCase {

    private final DishRepositoryPort dishRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final RestaurantRepositoryPort restaurantRepositoryPort;
    private final OwnerValidatorPort ownerValidatorPort;
    private final AuthenticationProviderPort authenticationProviderPort;

    @Override
    public Dish createDish(CreateDishCommand command) {
        if (!ownerValidatorPort.isOwnerOfRestaurant(authenticationProviderPort.getAuthenticatedUserId(), command.getRestaurantId())) {
            throw new InvalidOwnerException("Solo el propietario del restaurante puede crear platos.");
        }

        Category category = categoryRepositoryPort.findById(command.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.getCategoryId()));

        Restaurant restaurant = restaurantRepositoryPort.findById(command.getRestaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(command.getRestaurantId()));

        Dish dish = Dish.builder()
                .name(command.getName())
                .price(command.getPrice())
                .description(command.getDescription())
                .imageUrl(command.getImageUrl())
                .category(category)
                .restaurant(restaurant)
                .active(true)
                .build();

        return dishRepositoryPort.save(dish);
    }
}
