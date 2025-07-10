package com.pragma.plazoleta.dish.application.service;

import com.pragma.plazoleta.dish.application.dto.UpdateDishCommand;
import com.pragma.plazoleta.dish.application.exception.DishNotFoundException;
import com.pragma.plazoleta.dish.domain.port.output.OwnerOfRestaurantValidatorPort;
import com.pragma.plazoleta.restaurant.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.dish.domain.model.Dish;
import com.pragma.plazoleta.dish.application.port.input.UpdateDishUseCase;
import com.pragma.plazoleta.dish.domain.port.output.DishRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateDishUseCaseImpl implements UpdateDishUseCase {

    private final DishRepositoryPort dishRepositoryPort;
    private final OwnerOfRestaurantValidatorPort ownerOfRestaurantValidatorPort;

    @Override
    public Dish updateDish(Long authenticatedUserId, Long dishId, UpdateDishCommand command) {
        Dish dish = dishRepositoryPort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));

        if (!ownerOfRestaurantValidatorPort.isOwnerOfRestaurant(authenticatedUserId, dish.getRestaurant().getId())) {
            throw new InvalidOwnerException("No tiene permiso para actualizar este plato.");
        }

        dish.setPrice(command.getPrice());
        dish.setDescription(command.getDescription());

        return dishRepositoryPort.save(dish);
    }
}
