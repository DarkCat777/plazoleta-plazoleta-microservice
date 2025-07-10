package com.pragma.plazoleta.restaurant.domain.port.output;

import com.pragma.plazoleta.shared.dto.PaginationResult;
import com.pragma.plazoleta.shared.dto.PaginationQuery;
import com.pragma.plazoleta.restaurant.domain.model.Restaurant;

import java.util.Optional;

public interface RestaurantRepositoryPort {
    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(Long restaurantId);

    PaginationResult<Restaurant> findAllPaged(PaginationQuery pageable);
}
