package com.pragma.plazoleta.domain.port.input;

import com.pragma.plazoleta.application.dto.CreateRestaurantCommand;
import com.pragma.plazoleta.domain.model.Restaurant;

public interface CreateRestaurantUseCase {
    Restaurant createRestaurant(CreateRestaurantCommand command);
}
