package com.pragma.plazoleta.dish.application.port.input;

import com.pragma.plazoleta.dish.application.dto.UpdateDishCommand;
import com.pragma.plazoleta.dish.domain.model.Dish;

public interface UpdateDishUseCase {
    Dish execute(Long authenticatedUserId, Long dishId, UpdateDishCommand command);
}
