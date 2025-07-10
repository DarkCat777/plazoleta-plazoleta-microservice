package com.pragma.plazoleta.restaurant.application.port.input;

import com.pragma.plazoleta.shared.dto.PaginationResult;
import com.pragma.plazoleta.shared.dto.PaginationQuery;
import com.pragma.plazoleta.restaurant.domain.model.Restaurant;

public interface GetPagedRestaurantUseCase {

    PaginationResult<Restaurant> findAllPaginated(PaginationQuery query);
}
