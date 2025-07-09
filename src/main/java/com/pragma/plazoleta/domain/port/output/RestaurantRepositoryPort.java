package com.pragma.plazoleta.domain.port.output;

import com.pragma.plazoleta.application.dto.PaginatedResult;
import com.pragma.plazoleta.application.dto.PaginationQuery;
import com.pragma.plazoleta.domain.model.Restaurant;

import java.util.Optional;

public interface RestaurantRepositoryPort {
    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(Long restaurantId);

    PaginatedResult<Restaurant> findAllPaged(PaginationQuery pageable);
}
