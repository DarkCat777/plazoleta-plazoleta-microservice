package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.OrderCantCanceledNotification;
import com.pragma.plazoleta.domain.model.OrderReadyNotification;

public interface NotificationClientPort {
    void notifyOrderReady(OrderReadyNotification orderReadyNotification);

    void notifyOrderCantCancelled(OrderCantCanceledNotification orderCantCanceledNotification);
}
