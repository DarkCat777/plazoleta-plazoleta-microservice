package com.pragma.plazoleta.domain.port.output;

import com.pragma.plazoleta.domain.model.Restaurant;

public interface RestaurantRepository {
    Restaurant save(Restaurant restaurant);
}
