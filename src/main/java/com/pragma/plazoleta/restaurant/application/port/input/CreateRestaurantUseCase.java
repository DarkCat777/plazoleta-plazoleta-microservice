package com.pragma.plazoleta.restaurant.application.port.input;

import com.pragma.plazoleta.restaurant.application.dto.CreateRestaurantCommand;
import com.pragma.plazoleta.restaurant.domain.model.Restaurant;

public interface CreateRestaurantUseCase {
    Restaurant createRestaurant(CreateRestaurantCommand command);
}
