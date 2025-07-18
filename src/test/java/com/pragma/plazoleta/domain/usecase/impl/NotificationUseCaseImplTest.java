package com.pragma.plazoleta.domain.usecase.impl;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderCantCanceledNotification;
import com.pragma.plazoleta.domain.model.OrderReadyNotification;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.NotificationClientPort;
import com.pragma.plazoleta.domain.spi.UserClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NotificationUseCaseImplTest {

    private UserClientPort userClientPort;
    private NotificationClientPort notificationClientPort;
    private NotificationUseCaseImpl notificationUseCase;

    @BeforeEach
    void setUp() {
        userClientPort = mock(UserClientPort.class);
        notificationClientPort = mock(NotificationClientPort.class);
        notificationUseCase = new NotificationUseCaseImpl(userClientPort, notificationClientPort);
    }

    @Test
    void sendNotificationOfOrderReady_success() {
        Order order = Order.builder()
                .id(1L)
                .customerId(10L)
                .securityPin("1234")
                .build();

        User customer = new User();
        customer.setId(10L);
        customer.setPhoneNumber("+51987654321");

        when(userClientPort.getUserById(10L)).thenReturn(Optional.of(customer));

        notificationUseCase.sendNotificationOfOrderReady(order);

        ArgumentCaptor<OrderReadyNotification> captor = ArgumentCaptor.forClass(OrderReadyNotification.class);
        verify(notificationClientPort).notifyOrderReady(captor.capture());

        OrderReadyNotification notification = captor.getValue();
        assertEquals(1L, notification.getOrderId());
        assertEquals("+51987654321", notification.getCustomerPhone());
        assertEquals("1234", notification.getSecurityPin());
    }

    @Test
    void sendNotificationOfOrderReady_userNotFound_shouldNotThrow() {
        Order order = Order.builder().id(1L).customerId(99L).securityPin("1234").build();

        when(userClientPort.getUserById(99L)).thenReturn(Optional.empty());

        notificationUseCase.sendNotificationOfOrderReady(order);

        verify(notificationClientPort, never()).notifyOrderReady(any());
    }

    @Test
    void sendNotificationOfOrderReady_notificationFails_shouldNotThrow() {
        Order order = Order.builder()
                .id(1L)
                .customerId(10L)
                .securityPin("1234")
                .build();

        User customer = new User();
        customer.setId(10L);
        customer.setPhoneNumber("+51987654321");

        when(userClientPort.getUserById(10L)).thenReturn(Optional.of(customer));
        doThrow(new RuntimeException("SMS error"))
                .when(notificationClientPort).notifyOrderReady(any());

        notificationUseCase.sendNotificationOfOrderReady(order);

        verify(notificationClientPort).notifyOrderReady(any());
    }

    @Test
    void sendNotificationOfOrderInPreparation_success() {
        Order order = Order.builder()
                .id(2L)
                .customerId(20L)
                .status(OrderStatus.IN_PREPARATION)
                .build();

        User customer = new User();
        customer.setId(20L);
        customer.setPhoneNumber("+51912345678");

        when(userClientPort.getUserById(20L)).thenReturn(Optional.of(customer));

        notificationUseCase.sendNotificationOfOrderInPreparation(order);

        ArgumentCaptor<OrderCantCanceledNotification> captor = ArgumentCaptor.forClass(OrderCantCanceledNotification.class);
        verify(notificationClientPort).notifyOrderCantCancelled(captor.capture());

        OrderCantCanceledNotification notification = captor.getValue();
        assertEquals(2L, notification.getOrderId());
        assertEquals(OrderStatus.IN_PREPARATION, notification.getOrderStatus());
        assertEquals("+51912345678", notification.getCustomerPhone());
    }

    @Test
    void sendNotificationOfOrderInPreparation_userNotFound_shouldNotThrow() {
        Order order = Order.builder()
                .id(2L)
                .customerId(99L)
                .status(OrderStatus.IN_PREPARATION)
                .build();

        when(userClientPort.getUserById(99L)).thenReturn(Optional.empty());

        notificationUseCase.sendNotificationOfOrderInPreparation(order);

        verify(notificationClientPort, never()).notifyOrderCantCancelled(any());
    }

    @Test
    void sendNotificationOfOrderInPreparation_notificationFails_shouldNotThrow() {
        Order order = Order.builder()
                .id(2L)
                .customerId(20L)
                .status(OrderStatus.IN_PREPARATION)
                .build();

        User customer = new User();
        customer.setId(20L);
        customer.setPhoneNumber("+51912345678");

        when(userClientPort.getUserById(20L)).thenReturn(Optional.of(customer));
        doThrow(new RuntimeException("Twilio fail"))
                .when(notificationClientPort).notifyOrderCantCancelled(any());

        notificationUseCase.sendNotificationOfOrderInPreparation(order);

        verify(notificationClientPort).notifyOrderCantCancelled(any());
    }
}
