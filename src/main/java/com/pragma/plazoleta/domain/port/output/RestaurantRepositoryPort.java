package com.pragma.plazoleta.domain.port.output;

import com.pragma.plazoleta.domain.model.Restaurant;

import java.util.Optional;

public interface RestaurantRepositoryPort {
    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(Long restaurantId);
}
