package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.Dish;
import com.pragma.plazoleta.infrastructure.output.jpa.model.JpaDishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {CategoryEntityMapper.class})
public interface DishEntityMapper {

    Dish toDomain(JpaDishEntity entity);

    JpaDishEntity toEntity(Dish domain);

}
