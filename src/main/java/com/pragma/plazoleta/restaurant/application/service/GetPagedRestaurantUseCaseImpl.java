package com.pragma.plazoleta.restaurant.application.service;

import com.pragma.plazoleta.shared.dto.PaginationResult;
import com.pragma.plazoleta.shared.dto.PaginationQuery;
import com.pragma.plazoleta.restaurant.domain.model.Restaurant;
import com.pragma.plazoleta.restaurant.application.port.input.GetPagedRestaurantUseCase;
import com.pragma.plazoleta.restaurant.domain.port.output.RestaurantRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetPagedRestaurantUseCaseImpl implements GetPagedRestaurantUseCase {

    private final RestaurantRepositoryPort restaurantRepositoryPort;

    @Override
    public PaginationResult<Restaurant> findAllPaginated(PaginationQuery query) {
        return restaurantRepositoryPort.findAllPaged(query);
    }
}
