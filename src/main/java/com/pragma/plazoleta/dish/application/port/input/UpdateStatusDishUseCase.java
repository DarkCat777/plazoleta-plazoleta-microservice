package com.pragma.plazoleta.dish.application.port.input;

import com.pragma.plazoleta.dish.application.dto.UpdateStatusDishCommand;
import com.pragma.plazoleta.dish.domain.model.Dish;

public interface UpdateStatusDishUseCase {
    Dish execute(Long authenticatedUserId, Long dishId, UpdateStatusDishCommand command);
}
