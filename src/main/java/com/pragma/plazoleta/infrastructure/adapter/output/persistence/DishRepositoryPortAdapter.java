package com.pragma.plazoleta.infrastructure.adapter.output.persistence;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.domain.port.output.DishRepositoryPort;
import com.pragma.plazoleta.infrastructure.adapter.mapper.DishEntityMapper;
import com.pragma.plazoleta.infrastructure.adapter.output.repository.JpaDishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DishRepositoryPortAdapter implements DishRepositoryPort {

    private final JpaDishRepository dishRepository;
    private final DishEntityMapper dishMapper;

    @Override
    public Dish save(Dish dish) {
        return dishMapper.toDomain(dishRepository.save(dishMapper.toEntity(dish)));
    }

    @Override
    public Optional<Dish> findById(Long dishId) {
        return dishRepository.findById(dishId).map(dishMapper::toDomain);
    }
}
