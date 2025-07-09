package com.pragma.plazoleta.application.port.input;

import com.pragma.plazoleta.application.dto.UpdateActiveOrInactiveCommand;
import com.pragma.plazoleta.domain.model.Dish;

public interface UpdateActiveOrInactiveDishUseCase {
    Dish updateDishActiveOrInactive(Long dishId, UpdateActiveOrInactiveCommand command);
}
