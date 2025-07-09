package com.pragma.plazoleta.application.port.input;

import com.pragma.plazoleta.application.dto.PaginatedResult;
import com.pragma.plazoleta.application.dto.PaginationQuery;
import com.pragma.plazoleta.domain.model.Restaurant;

public interface GetPagedRestaurantUseCase {

    PaginatedResult<Restaurant> findAllPaginated(PaginationQuery query);
}
