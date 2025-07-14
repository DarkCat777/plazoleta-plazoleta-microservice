package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.application.mapper.PaginationQueryMapper;
import com.pragma.plazoleta.application.mapper.PaginationResultMapper;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.spi.persistence.RestaurantRepositoryPort;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.RestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.JpaRestaurantRepository;
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

    @Override
    public boolean existsById(Long restaurantId) {
        return repository.existsById(restaurantId);
    }

    @Override
    public Optional<Restaurant> findByOwnerId(Long ownerId) {
        return repository.findByOwnerId(ownerId).map(entityMapper::toDomain);
    }

}

