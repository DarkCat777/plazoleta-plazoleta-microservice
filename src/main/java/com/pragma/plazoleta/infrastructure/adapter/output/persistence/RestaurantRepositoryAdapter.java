package com.pragma.plazoleta.infrastructure.adapter.output.persistence;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.port.output.RestaurantRepositoryPort;
import com.pragma.plazoleta.infrastructure.adapter.mapper.RestaurantEntityMapper;
import com.pragma.plazoleta.infrastructure.adapter.output.repository.JpaRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

}

