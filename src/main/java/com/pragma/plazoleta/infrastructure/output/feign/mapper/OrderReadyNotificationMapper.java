package com.pragma.plazoleta.infrastructure.output.feign.mapper;

import com.pragma.plazoleta.domain.model.OrderReadyNotification;
import com.pragma.plazoleta.infrastructure.output.feign.model.request.OrderReadyNotificationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderReadyNotificationMapper {

    OrderReadyNotificationRequest toRequest(OrderReadyNotification orderReadyNotification);

}
