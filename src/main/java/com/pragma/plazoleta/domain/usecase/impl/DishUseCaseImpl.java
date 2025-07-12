package com.pragma.plazoleta.domain.usecase.impl;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
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
import com.pragma.plazoleta.domain.validation.FieldValidationError;
import com.pragma.plazoleta.domain.validation.exception.ValidationException;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.pragma.plazoleta.domain.validation.ValidationUtils.*;

@RequiredArgsConstructor
public class DishUseCaseImpl implements DishUseCase {

    private final DishRepositoryPort dishRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final RestaurantRepositoryPort restaurantRepositoryPort;
    private final OwnerValidatorPort ownerValidatorPort;

    private void validateCreateDish(Long ownerId, Dish command) {
        List<FieldValidationError> errors = new ArrayList<>();

        errors.add(validateNotBlank("name", command.getName()));
        errors.add(validateNotBlank("description", command.getDescription()));
        errors.add(validatePositive("price", command.getPrice()));
        errors.add(validateNotBlank("imageUrl", command.getImageUrl()));

        errors.add(validateNotNull("categoryId", command.getCategory()));
        errors.add(validateNotNull("categoryId", command.getCategory() != null ? command.getCategory().getId() : null));
        errors.add(validateNotNull("restaurantId", command.getRestaurant()));
        errors.add(validateNotNull("restaurantId", command.getRestaurant() != null ? command.getRestaurant().getId() : null));

        // Elimina los nulls
        errors.removeIf(Objects::isNull);

        // Si hay errores, los lanzas
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    @Override
    public Dish createDish(Long ownerId, Dish command) {
        validateCreateDish(ownerId, command);

        if (!ownerValidatorPort.isOwnerOfRestaurant(ownerId, command.getRestaurant().getId())) {
            throw new InvalidOwnerException("Solo el propietario del restaurante puede crear platos.");
        }

        Category category = categoryRepositoryPort.findById(command.getCategory().getId())
                .orElseThrow(() -> new CategoryNotFoundException(command.getCategory().getId()));

        Restaurant restaurant = restaurantRepositoryPort.findById(command.getRestaurant().getId())
                .orElseThrow(() -> new RestaurantNotFoundException(command.getRestaurant().getId()));

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

    private void validateUpdateDish(Dish command) {
        List<FieldValidationError> errors = new ArrayList<>();

        errors.add(validatePositive("price", command.getPrice()));
        errors.add(validateNotBlank("description", command.getDescription()));

        errors.removeIf(Objects::isNull);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
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
        List<FieldValidationError> errors = new ArrayList<>();

        errors.add(validateNotNull("active", command.isActive()));
        errors.removeIf(Objects::isNull);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    @Override
    public Dish updateDishStatus(Long ownerId, Long dishId, Dish command) {
        validateUpdateDishStatus(command);

        Dish dish = dishRepositoryPort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));

        if (!ownerValidatorPort.isOwnerOfRestaurant(ownerId, dish.getRestaurant().getId())) {
            throw new InvalidOwnerException("Solo el propietario del restaurante puede actualizar el estado de los platos.");
        }

        dish.setActive(command.isActive());

        return dishRepositoryPort.save(dish);
    }

    @Override
    public PaginationResult<Dish> getPagedDishByRestaurantIdAndCategoryId(Long restaurantId, Long categoryId, PaginationQuery paginationQuery) {
        return dishRepositoryPort.findAllByRestaurantIdAndCategoryId(restaurantId, categoryId, paginationQuery);
    }
}
