package com.pragma.plazoleta.dish.application.service;

import com.pragma.plazoleta.dish.application.dto.UpdateStatusDishCommand;
import com.pragma.plazoleta.dish.application.exception.DishNotFoundException;
import com.pragma.plazoleta.dish.domain.port.output.OwnerOfRestaurantValidatorPort;
import com.pragma.plazoleta.restaurant.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.dish.application.port.input.UpdateStatusDishUseCase;
import com.pragma.plazoleta.dish.domain.model.Dish;
import com.pragma.plazoleta.dish.domain.port.output.DishRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateStatusDishUseCaseImpl implements UpdateStatusDishUseCase {

    private final DishRepositoryPort dishRepositoryPort;
    private final OwnerOfRestaurantValidatorPort ownerOfRestaurantValidatorPort;

    @Override
    public Dish updateDishStatus(Long authenticatedUserId,Long dishId, UpdateStatusDishCommand command) {
        Dish dish = dishRepositoryPort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));
        if (!ownerOfRestaurantValidatorPort.isOwnerOfRestaurant(authenticatedUserId, dish.getRestaurant().getId())) {
            throw new InvalidOwnerException("Solo el propietario del restaurante puede actualizar el estado de los platos.");
        }
        dish.setActive(command.getActive());
        return dishRepositoryPort.save(dish);
    }
}
