package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.domain.model.Restaurant;

public interface RestaurantUseCase {

    Restaurant createRestaurant(Restaurant command);

    PaginationResult<Restaurant> findAllPaginated(PaginationQuery query);

    Restaurant findByOwnerId(Long ownerId);
}
