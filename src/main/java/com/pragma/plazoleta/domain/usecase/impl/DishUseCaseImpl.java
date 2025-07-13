package com.pragma.plazoleta.domain.usecase.impl;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.domain.validation.Validation;
import com.pragma.plazoleta.domain.exception.CategoryNotFoundException;
import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.InvalidOwnerException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.OwnerValidatorPort;
import com.pragma.plazoleta.domain.spi.persistence.CategoryRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.DishRepositoryPort;
import com.pragma.plazoleta.domain.spi.persistence.RestaurantRepositoryPort;
import com.pragma.plazoleta.domain.usecase.DishUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DishUseCaseImpl implements DishUseCase {

    private final DishRepositoryPort dishRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final RestaurantRepositoryPort restaurantRepositoryPort;
    private final OwnerValidatorPort ownerValidatorPort;

    private void validateCreateDish(Long ownerId, Dish dish) {
        Validation.builder(dish)
                .notNull("ownerId", d -> ownerId)
                .notBlank("name", Dish::getName)
                .notBlank("description", Dish::getDescription)
                .positive("price", Dish::getPrice)
                .notBlank("imageUrl", Dish::getImageUrl)
                .nested("category", Dish::getCategory, v -> v
                        .notNull("id", Category::getId)
                        .positive("id", Category::getId)
                        .build()
                )
                .nested("restaurant", Dish::getRestaurant, v -> v
                        .notNull("id", Restaurant::getId)
                        .positive("id", Restaurant::getId)
                        .build()
                )
                .build()
                .validate();
    }

    @Override
    public Dish createDish(Long ownerId, Dish dish) {
        validateCreateDish(ownerId, dish);

        Category category = categoryRepositoryPort.findById(dish.getCategory().getId())
                .orElseThrow(() -> new CategoryNotFoundException(dish.getCategory().getId()));

        Restaurant restaurant = restaurantRepositoryPort.findById(dish.getRestaurant().getId())
                .orElseThrow(() -> new RestaurantNotFoundException(dish.getRestaurant().getId()));

        if (!ownerValidatorPort.isOwnerOfRestaurant(ownerId, dish.getRestaurant().getId())) {
            throw new InvalidOwnerException("Solo el propietario del restaurante puede crear platos.");
        }

        dish.setCategory(category);
        dish.setRestaurant(restaurant);
        dish.setActive(true);

        return dishRepositoryPort.save(dish);
    }

    private void validateUpdateDish(Dish command) {
        Validation.builder(command)
                .notNull("price", Dish::getPrice)
                .positive("price", Dish::getPrice)
                .notNull("description", Dish::getDescription)
                .notBlank("description", Dish::getDescription)
                .build()
                .validate();
    }

    @Override
    public Dish updateDish(Long ownerId, Long dishId, Dish command) {
        validateUpdateDish(command);

        Dish dish = dishRepositoryPort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));

        if (!ownerValidatorPort.isOwnerOfRestaurant(ownerId, dish.getRestaurant().getId())) {
            throw new InvalidOwnerException("No tiene permiso para actualizar este plato.");
        }

        dish.setPrice(command.getPrice());
        dish.setDescription(command.getDescription());

        return dishRepositoryPort.save(dish);
    }

    private void validateUpdateDishStatus(Dish command) {
        Validation.builder(command)
                .notNull("active", Dish::getActive)
                .build()
                .validate();
    }

    @Override
    public Dish updateDishStatus(Long ownerId, Long dishId, Dish command) {
        validateUpdateDishStatus(command);

        Dish dish = dishRepositoryPort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));

        if (!ownerValidatorPort.isOwnerOfRestaurant(ownerId, dish.getRestaurant().getId())) {
            throw new InvalidOwnerException("Solo el propietario del restaurante puede actualizar el estado de los platos.");
        }

        dish.setActive(command.getActive());

        return dishRepositoryPort.save(dish);
    }

    @Override
    public PaginationResult<Dish> getPagedDishByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, PaginationQuery paginationQuery) {
        return dishRepositoryPort.findAllByRestaurantIdAndCategoryId(restaurantId, categoryId, paginationQuery);
    }
}
