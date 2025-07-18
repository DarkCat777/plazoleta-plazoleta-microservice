package com.pragma.plazoleta.domain.usecase.impl;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderCantCanceledNotification;
import com.pragma.plazoleta.domain.model.OrderReadyNotification;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.NotificationClientPort;
import com.pragma.plazoleta.domain.spi.UserClientPort;
import com.pragma.plazoleta.domain.usecase.NotificationUseCase;
import com.pragma.plazoleta.infrastructure.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class NotificationUseCaseImpl implements NotificationUseCase {

    private final UserClientPort userClientPort;
    private final NotificationClientPort notificationClientPort;

    @Override
    public void sendNotificationOfOrderReady(Order order) {
        try {
            User customer = userClientPort.getUserById(order.getCustomerId())
                    .orElseThrow(() -> new UserNotFoundException(order.getCustomerId()));
            notificationClientPort.notifyOrderReady(
                    new OrderReadyNotification(
                            order.getId(),
                            customer.getPhoneNumber(),
                            order.getSecurityPin()
                    )
            );
        } catch (UserNotFoundException e) {
            log.error("Error no se encontró los datos del cliente con Id {}: {}", order.getCustomerId(), e.getMessage());
        } catch (Exception e) {
            log.error("Error enviando notificación (Order READY) SMS para el pedido {}: {}", order.getId(), e.getMessage());
        }
    }

    @Override
    public void sendNotificationOfOrderInPreparation(Order order) {
        try {
            User customer = userClientPort.getUserById(order.getCustomerId())
                    .orElseThrow(() -> new UserNotFoundException(order.getCustomerId()));
            notificationClientPort.notifyOrderCantCancelled(
                    new OrderCantCanceledNotification(
                            order.getId(),
                            order.getStatus(),
                            customer.getPhoneNumber()
                    )
            );
        } catch (UserNotFoundException e) {
            log.error("Error no se encontró los datos del cliente con Id {}: {}", order.getCustomerId(), e.getMessage());
        } catch (Exception e) {
            log.error("Error enviando notificación (Order IN_PREPARATION) SMS para el pedido {}: {}", order.getId(), e.getMessage());
        }
    }

}
