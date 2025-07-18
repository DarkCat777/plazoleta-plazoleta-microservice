package com.pragma.plazoleta.infrastructure.output.feign.client;

import com.pragma.plazoleta.infrastructure.output.feign.model.request.OrderCantCanceledNotificationRequest;
import com.pragma.plazoleta.infrastructure.output.feign.model.request.OrderReadyNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notifications-microservice", url = "${feign.notifications.client.url}/notifications")
public interface NotificationFeignClient {

    @PostMapping("/notify/order-ready")
    Void notifyOrderReady(@Validated @RequestBody OrderReadyNotificationRequest notification);

    @PostMapping("/notify/order-cant-canceled")
    Void notifyOrderCantCanceled(@Validated @RequestBody OrderCantCanceledNotificationRequest notification);

}
