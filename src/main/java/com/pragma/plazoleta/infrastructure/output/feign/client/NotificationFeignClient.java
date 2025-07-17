package com.pragma.plazoleta.infrastructure.output.feign.client;

import com.pragma.plazoleta.infrastructure.output.feign.model.OrderNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notifications-microservice", url = "${notifications.client.url}/notifications")
public interface NotificationFeignClient {

    @PostMapping("/notify/order-ready")
    Void notifyOrderReady(@Validated @RequestBody OrderNotificationRequest notification);
}
