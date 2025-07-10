package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.application.port.input.GetPagedRestaurantUseCase;
import com.pragma.plazoleta.domain.port.output.RestaurantRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetPagedRestaurantUseCaseImpl implements GetPagedRestaurantUseCase {

    private final RestaurantRepositoryPort restaurantRepositoryPort;

    @Override
    public PaginationResult<Restaurant> findAllPaginated(PaginationQuery query) {
        return restaurantRepositoryPort.findAllPaged(query);
    }
}
