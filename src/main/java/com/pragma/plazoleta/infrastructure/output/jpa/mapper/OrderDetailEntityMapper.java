package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.OrderDetail;
import com.pragma.plazoleta.infrastructure.output.jpa.model.JpaDishEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.model.JpaOrderDetailEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.model.pk.JpaOrderDetailId;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.JpaDishRepository;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderDetailEntityMapper {

    @Mapping(target = "orderId", source = "id.orderId")
    @Mapping(target = "dishId", source = "id.dishId")
    OrderDetail toDomain(JpaOrderDetailEntity entity);

    @Mapping(target = "id", ignore = true)  // lo mapeamos de la consulta al repository con los ids
    @Mapping(target = "order", ignore = true) // lo mapeamos de la consulta al repository
    @Mapping(target = "dish", ignore = true) // lo mapeamos de la consulta al repository
    JpaOrderDetailEntity toEntity(OrderDetail detail);

}
