package com.pragma.plazoleta.dish.application.service;

import com.pragma.plazoleta.dish.application.dto.CreateDishCommand;
import com.pragma.plazoleta.dish.application.exception.CategoryNotFoundException;
import com.pragma.plazoleta.dish.domain.port.output.OwnerOfRestaurantValidatorPort;
import com.pragma.plazoleta.restaurant.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.restaurant.application.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.dish.application.port.input.CreateDishUseCase;
import com.pragma.plazoleta.dish.domain.model.Category;
import com.pragma.plazoleta.dish.domain.model.Dish;
import com.pragma.plazoleta.restaurant.domain.model.Restaurant;
import com.pragma.plazoleta.dish.domain.port.output.CategoryRepositoryPort;
import com.pragma.plazoleta.dish.domain.port.output.DishRepositoryPort;
import com.pragma.plazoleta.restaurant.domain.port.output.RestaurantRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateDishUseCaseImpl implements CreateDishUseCase {

    private final DishRepositoryPort dishRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final RestaurantRepositoryPort restaurantRepositoryPort;
    private final OwnerOfRestaurantValidatorPort ownerOfRestaurantValidatorPort;

    @Override
    public Dish createDish(Long authenticatedUserId, CreateDishCommand command) {
        if (!ownerOfRestaurantValidatorPort.isOwnerOfRestaurant(authenticatedUserId, command.getRestaurantId())) {
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
