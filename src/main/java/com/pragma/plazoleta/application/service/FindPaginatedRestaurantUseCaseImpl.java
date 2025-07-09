package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.PaginatedResult;
import com.pragma.plazoleta.application.dto.PaginationQuery;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.application.port.input.FindPaginatedRestaurantUseCase;
import com.pragma.plazoleta.domain.port.output.RestaurantRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindPaginatedRestaurantUseCaseImpl implements FindPaginatedRestaurantUseCase {

    private final RestaurantRepositoryPort repository;

    @Override
    public PaginatedResult<Restaurant> findAllPaginated(PaginationQuery query) {
        return repository.findAllPaginated(query);
    }
}
