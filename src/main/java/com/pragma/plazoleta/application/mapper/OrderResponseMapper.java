package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.CreateOrderCommand;
import com.pragma.plazoleta.application.dto.response.OrderResponse;
import com.pragma.plazoleta.domain.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {OrderDetailResponseMapper.class, DishResponseMapper.class})
public interface OrderResponseMapper {

    @Mapping(target = "id", ignore = true) // En la creación no se tiene id
    @Mapping(target = "customerId", ignore = true) // Se asigna de usuario que inicio sesión
    @Mapping(target = "createdAt", ignore = true) // Se le asignara cuando ni bien se validen los datos
    @Mapping(target = "status", ignore = true) // Se le asigna uno por defecto cuando se crea
    @Mapping(target = "chefId", ignore = true) // Se le asignará uno cuando cambie de estado
    Order toDomain(CreateOrderCommand command);

    OrderResponse toResponse(Order order);

}
