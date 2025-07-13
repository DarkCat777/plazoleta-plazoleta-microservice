package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.infrastructure.output.jpa.model.JpaOrderEntity;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {OrderDetailEntityMapper.class})
public interface OrderEntityMapper {

    @Mapping(target = "restaurant", ignore = true) // son relaciones de jpa que deben ser asignadas de forma manual
    @Mapping(target = "dishes", ignore = true) // son relaciones de jpa que deben ser asignadas de forma manual
    JpaOrderEntity toEntity(Order order);

    @Mapping(source = "restaurant.id", target = "restaurantId")
    @Mapping(source = "dishes", target = "dishes")
    Order toDomain(JpaOrderEntity entity);
}
