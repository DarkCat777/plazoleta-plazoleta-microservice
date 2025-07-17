package com.pragma.plazoleta.infrastructure.output.feign.adapter;

import com.pragma.plazoleta.domain.model.OrderCantCanceledNotification;
import com.pragma.plazoleta.domain.model.OrderReadyNotification;
import com.pragma.plazoleta.domain.spi.NotificationClientPort;
import com.pragma.plazoleta.infrastructure.output.feign.client.NotificationFeignClient;
import com.pragma.plazoleta.infrastructure.output.feign.mapper.OrderCantCanceledNotificationMapper;
import com.pragma.plazoleta.infrastructure.output.feign.mapper.OrderReadyNotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationClientAdapter implements NotificationClientPort {

    private final NotificationFeignClient notificationFeignClient;
    private final OrderReadyNotificationMapper orderReadyNotificationMapper;
    private final OrderCantCanceledNotificationMapper orderCantCanceledNotificationMapper;

    @Override
    public void notifyOrderReady(OrderReadyNotification orderReadyNotification) {
        notificationFeignClient.notifyOrderReady(orderReadyNotificationMapper.toRequest(orderReadyNotification));
    }

    @Override
    public void notifyOrderCantCancelled(OrderCantCanceledNotification orderCantCanceledNotification) {
        notificationFeignClient.notifyOrderCantCanceled(orderCantCanceledNotificationMapper.toRequest(orderCantCanceledNotification));
    }
}
