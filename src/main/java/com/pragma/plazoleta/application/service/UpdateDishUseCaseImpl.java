package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.UpdateDishCommand;
import com.pragma.plazoleta.application.exception.DishNotFoundException;
import com.pragma.plazoleta.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.port.input.UpdateDishUseCase;
import com.pragma.plazoleta.domain.port.output.DishRepositoryPort;
import com.pragma.plazoleta.domain.port.output.OwnerValidatorPort;
import com.pragma.plazoleta.domain.port.output.UserPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateDishUseCaseImpl implements UpdateDishUseCase {

    private final DishRepositoryPort dishRepositoryPort;
    private final OwnerValidatorPort ownerValidatorPort;
    private final UserPort userPort;

    @Override
    public Dish updateDish(Long dishId, UpdateDishCommand command) {
        Dish dish = dishRepositoryPort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));

        Long ownerId = userPort.getAuthenticatedUserId();

        if (!ownerValidatorPort.isOwnerOfRestaurant(ownerId, dish.getRestaurant().getId())) {
            throw new InvalidOwnerException("No tiene permiso para actualizar este plato.");
        }

        dish.setPrice(command.getPrice());
        dish.setDescription(command.getDescription());

        return dishRepositoryPort.save(dish);
    }
}
