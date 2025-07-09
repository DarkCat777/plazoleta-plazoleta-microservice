package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.UpdateStatusDishCommand;
import com.pragma.plazoleta.application.exception.DishNotFoundException;
import com.pragma.plazoleta.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.application.port.input.UpdateStatusDishUseCase;
import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.port.output.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateStatusDishUseCaseImpl implements UpdateStatusDishUseCase {

    private final DishRepositoryPort dishRepositoryPort;
    private final OwnerValidatorPort validator;
    private final AuthenticationProviderPort authenticationProviderPort;

    @Override
    public Dish updateDishStatus(Long dishId, UpdateStatusDishCommand command) {
        Dish dish = dishRepositoryPort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));
        if (!validator.isOwnerOfRestaurant(authenticationProviderPort.getAuthenticatedUserId(), dish.getRestaurant().getId())) {
            throw new InvalidOwnerException("Solo el propietario del restaurante puede actualizar el estado de los platos.");
        }
        dish.setActive(command.getActive());
        return dishRepositoryPort.save(dish);
    }
}
