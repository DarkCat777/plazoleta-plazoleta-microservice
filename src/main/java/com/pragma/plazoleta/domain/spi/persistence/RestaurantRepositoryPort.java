package com.pragma.plazoleta.domain.spi.persistence;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.domain.model.Restaurant;

import java.util.Optional;

public interface RestaurantRepositoryPort {
    Restaurant save(Restaurant restaurant);

    Optional<Restaurant> findById(Long restaurantId);

    PaginationResult<Restaurant> findAllPaged(PaginationQuery pageable);

    boolean existsById(Long restaurantId);
}
