package com.pragma.plazoleta.application.port.input;

import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.domain.model.Restaurant;

public interface GetPagedRestaurantUseCase {

    PaginationResult<Restaurant> findAllPaginated(PaginationQuery query);
}
