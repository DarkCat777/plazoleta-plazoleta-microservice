package com.pragma.plazoleta.dish.application.port.input;

import com.pragma.plazoleta.dish.application.dto.CreateDishCommand;
import com.pragma.plazoleta.dish.domain.model.Dish;

public interface CreateDishUseCase {
    Dish createDish(Long authenticatedUserId, CreateDishCommand command);
}