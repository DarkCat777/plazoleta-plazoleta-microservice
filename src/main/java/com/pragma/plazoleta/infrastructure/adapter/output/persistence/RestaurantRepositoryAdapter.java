package com.pragma.plazoleta.infrastructure.adapter.output.persistence;

import com.pragma.plazoleta.application.dto.PaginatedResult;
import com.pragma.plazoleta.application.dto.PaginationQuery;
import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.port.output.RestaurantRepositoryPort;
import com.pragma.plazoleta.infrastructure.adapter.mapper.RestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.adapter.output.model.JpaRestaurantEntity;
import com.pragma.plazoleta.infrastructure.adapter.output.repository.JpaRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RestaurantRepositoryAdapter implements RestaurantRepositoryPort {

    private final JpaRestaurantRepository repository;
    private final RestaurantEntityMapper mapper;

    @Override
    public Restaurant save(Restaurant restaurant) {
        return mapper.toDomain(repository.save(mapper.toEntity(restaurant)));
    }

    @Override
    public Optional<Restaurant> findById(Long restaurantId) {
        return repository.findById(restaurantId).map(mapper::toDomain);
    }

    @Override
    public PaginatedResult<Restaurant> findAllPaginated(PaginationQuery query) {
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
        Page<JpaRestaurantEntity> page = repository.findAll(pageable);
        List<Restaurant> restaurants = page.getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();
        return new PaginatedResult<>(restaurants, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
    
}

