package com.pragma.plazoleta.infrastructure.output.feign.mapper;

import com.pragma.plazoleta.domain.model.OrderCantCanceledNotification;
import com.pragma.plazoleta.infrastructure.output.feign.model.request.OrderCantCanceledNotificationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderCantCanceledNotificationMapper {

    OrderCantCanceledNotificationRequest toRequest(OrderCantCanceledNotification orderCantCanceledNotification);

}
