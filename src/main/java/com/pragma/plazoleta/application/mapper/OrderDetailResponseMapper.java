package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.CreateOrderDetailCommand;
import com.pragma.plazoleta.domain.model.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderDetailResponseMapper {

    @Mapping(target = "orderId", ignore = true) // No existe en la creación de la orden
    OrderDetail toDomain(CreateOrderDetailCommand orderDetail);

}
