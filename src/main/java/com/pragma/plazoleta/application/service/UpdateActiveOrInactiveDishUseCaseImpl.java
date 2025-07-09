package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.UpdateActiveOrInactiveCommand;
import com.pragma.plazoleta.application.exception.DishNotFoundException;
import com.pragma.plazoleta.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.application.port.input.UpdateActiveOrInactiveDishUseCase;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.port.output.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateActiveOrInactiveDishUseCaseImpl implements UpdateActiveOrInactiveDishUseCase {

    private final DishRepositoryPort dishRepositoryPort;
    private final OwnerValidatorPort validator;
    private final UserPort userPort;

    @Override
    public Dish updateDishActiveOrInactive(Long dishId, UpdateActiveOrInactiveCommand command) {
        Dish dish = dishRepositoryPort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));
        if (!validator.isOwnerOfRestaurant(userPort.getAuthenticatedUserId(), dish.getRestaurant().getId())) {
            throw new InvalidOwnerException("Solo el propietario del restaurante puede actualizar el estado de los platos.");
        }
        dish.setActive(command.getActive());
        return dishRepositoryPort.save(dish);
    }
}
