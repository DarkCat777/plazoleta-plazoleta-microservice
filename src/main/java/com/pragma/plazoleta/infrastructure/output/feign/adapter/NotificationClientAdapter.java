package com.pragma.plazoleta.infrastructure.output.feign.adapter;

import com.pragma.plazoleta.domain.model.OrderReadyNotification;
import com.pragma.plazoleta.domain.spi.NotificationClientPort;
import com.pragma.plazoleta.infrastructure.output.feign.client.NotificationFeignClient;
import com.pragma.plazoleta.infrastructure.output.feign.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationClientAdapter implements NotificationClientPort {

    private final NotificationFeignClient notificationFeignClient;
    private final NotificationMapper notificationMapper;

    @Override
    public void notifyOrderReady(OrderReadyNotification orderReadyNotification) {
        notificationFeignClient.notifyOrderReady(notificationMapper.toRequest(orderReadyNotification));
    }
}
