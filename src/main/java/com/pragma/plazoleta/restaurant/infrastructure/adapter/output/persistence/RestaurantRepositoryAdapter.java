package com.pragma.plazoleta.restaurant.infrastructure.adapter.output.persistence;

import com.pragma.plazoleta.shared.dto.PaginationResult;
import com.pragma.plazoleta.shared.dto.PaginationQuery;
import com.pragma.plazoleta.restaurant.domain.model.Restaurant;
import com.pragma.plazoleta.restaurant.domain.port.output.RestaurantRepositoryPort;
import com.pragma.plazoleta.shared.mapper.PaginationQueryMapper;
import com.pragma.plazoleta.shared.mapper.PaginationResultMapper;
import com.pragma.plazoleta.restaurant.infrastructure.adapter.mapper.RestaurantEntityMapper;
import com.pragma.plazoleta.restaurant.infrastructure.adapter.output.repository.JpaRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RestaurantRepositoryAdapter implements RestaurantRepositoryPort {

    private final JpaRestaurantRepository repository;
    private final RestaurantEntityMapper entityMapper;
    private final PaginationQueryMapper paginationQueryMapper;
    private final PaginationResultMapper paginationResultMapper;

    @Override
    public Restaurant save(Restaurant restaurant) {
        return entityMapper.toDomain(repository.save(entityMapper.toEntity(restaurant)));
    }

    @Override
    public Optional<Restaurant> findById(Long restaurantId) {
        return repository.findById(restaurantId).map(entityMapper::toDomain);
    }

    @Override
    public PaginationResult<Restaurant> findAllPaged(PaginationQuery query) {
        Pageable pageable = paginationQueryMapper.toPageable(query);
        Page<Restaurant> page = repository.findAll(pageable).map(entityMapper::toDomain);
        return paginationResultMapper.toPaginatedResult(page);
    }

}

