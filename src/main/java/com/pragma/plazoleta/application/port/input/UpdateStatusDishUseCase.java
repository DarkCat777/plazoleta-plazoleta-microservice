package com.pragma.plazoleta.application.port.input;

import com.pragma.plazoleta.application.dto.UpdateStatusDishCommand;
import com.pragma.plazoleta.domain.model.Dish;

public interface UpdateStatusDishUseCase {
    Dish updateDishStatus(Long dishId, UpdateStatusDishCommand command);
}
