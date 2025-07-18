package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.model.Order;

public interface NotificationUseCase {

    void sendNotificationOfOrderReady(Order order);

    void sendNotificationOfOrderInPreparation(Order order);

}
