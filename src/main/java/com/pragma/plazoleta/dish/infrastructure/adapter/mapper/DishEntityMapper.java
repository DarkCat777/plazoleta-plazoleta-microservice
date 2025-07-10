package com.pragma.plazoleta.dish.infrastructure.adapter.mapper;

import com.pragma.plazoleta.dish.domain.model.Dish;
import com.pragma.plazoleta.dish.infrastructure.adapter.output.model.JpaDishEntity;
import com.pragma.plazoleta.shared.mapper.BaseEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {CategoryEntityMapper.class})
public interface DishEntityMapper extends BaseEntityMapper<Dish, JpaDishEntity> {
}
