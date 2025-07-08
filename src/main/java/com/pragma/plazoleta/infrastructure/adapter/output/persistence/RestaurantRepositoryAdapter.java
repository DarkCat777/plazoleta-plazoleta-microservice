package com.pragma.plazoleta.infrastructure.adapter.output.persistence;

import com.pragma.plazoleta.domain.model.Restaurant;
import com.pragma.plazoleta.domain.port.output.RestaurantRepository;
import com.pragma.plazoleta.infrastructure.adapter.mapper.RestaurantMapper;
import com.pragma.plazoleta.infrastructure.adapter.output.repository.JpaRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RestaurantRepositoryAdapter implements RestaurantRepository {

    private final JpaRestaurantRepository repository;
    private final RestaurantMapper mapper;

    @Override
    public Restaurant save(Restaurant restaurant) {
        return mapper.toDomain(repository.save(mapper.toEntity(restaurant)));
    }

    @Override
    public Optional<Restaurant> findById(Long restaurantId) {
        return repository.findById(restaurantId).map(mapper::toDomain);
    }

}

