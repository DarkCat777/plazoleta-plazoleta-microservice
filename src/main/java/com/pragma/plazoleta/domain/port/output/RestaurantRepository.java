package com.pragma.plazoleta.domain.port.output;

import com.pragma.plazoleta.domain.model.Restaurant;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public interface RestaurantRepository {
    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(@NotNull Long restaurantId);
}
