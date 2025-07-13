package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.OrderDetail;
import com.pragma.plazoleta.infrastructure.output.jpa.model.JpaOrderDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = DishEntityMapper.class)
public interface OrderDetailEntityMapper {

    @Mapping(target = "orderId", source = "id.orderId")
    OrderDetail toDomain(JpaOrderDetailEntity entity);

    @Mapping(target = "id", ignore = true)  // lo mapeamos de la consulta al repository con los ids
    @Mapping(target = "order", ignore = true) // lo mapeamos de la consulta al repository
    @Mapping(target = "dish", ignore = true) // lo mapeamos de la consulta al repository
    JpaOrderDetailEntity toEntity(OrderDetail detail);

}
