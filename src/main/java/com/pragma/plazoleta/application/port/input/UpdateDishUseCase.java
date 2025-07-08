package com.pragma.plazoleta.application.port.input;

import com.pragma.plazoleta.application.dto.UpdateDishCommand;
import com.pragma.plazoleta.domain.model.Dish;

public interface UpdateDishUseCase {
    Dish updateDish(Long dishId, UpdateDishCommand command);
}
