package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.OrderReadyNotification;

public interface NotificationClientPort {
    void notifyOrderReady(OrderReadyNotification orderReadyNotification);
}
