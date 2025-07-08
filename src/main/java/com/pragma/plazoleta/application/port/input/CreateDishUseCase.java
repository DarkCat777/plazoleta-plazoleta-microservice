package com.pragma.plazoleta.application.port.input;

import com.pragma.plazoleta.application.dto.CreateDishCommand;
import com.pragma.plazoleta.domain.model.Dish;

public interface CreateDishUseCase {
    Dish createDish(CreateDishCommand command);
}