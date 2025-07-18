package com.pragma.plazoleta.domain.usecase.impl;


import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.OrderTrace;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.TraceClientPort;
import com.pragma.plazoleta.domain.spi.UserClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TraceUseCaseImplTest {

    private UserClientPort userClientPort;
    private TraceClientPort traceClientPort;
    private TraceUseCaseImpl traceUseCase;

    @BeforeEach
    void setUp() {
        userClientPort = mock(UserClientPort.class);
        traceClientPort = mock(TraceClientPort.class);
        traceUseCase = new TraceUseCaseImpl(userClientPort, traceClientPort);
    }

    @Test
    void traceChangeStatusOrder_success() {
        // Arrange
        User customer = User.builder()
                .id(100L)
                .email("customer@test.com")
                .build();

        User employee = User.builder()
                .id(200L)
                .email("employee@test.com")
                .build();

        Order order = Order.builder()
                .id(1L)
                .customerId(100L)
                .build();

        when(userClientPort.getUserById(100L)).thenReturn(Optional.of(customer));
        when(userClientPort.getUserById(200L)).thenReturn(Optional.of(employee));

        // Act
        traceUseCase.traceChangeStatusOrder(order, 200L, OrderStatus.PENDING, OrderStatus.IN_PREPARATION);

        // Assert
        ArgumentCaptor<OrderTrace> captor = ArgumentCaptor.forClass(OrderTrace.class);
        verify(traceClientPort).registerOrderTrace(captor.capture());

        OrderTrace trace = captor.getValue();
        assertEquals(1L, trace.getOrderId());
        assertEquals(OrderStatus.PENDING, trace.getPreviousStatus());
        assertEquals(OrderStatus.IN_PREPARATION, trace.getNewStatus());
        assertEquals("customer@test.com", trace.getCustomer().getEmail());
        assertEquals("employee@test.com", trace.getEmployee().getEmail());
    }

    @Test
    void traceChangeStatusOrder_userNotFound_shouldNotThrow() {
        // Arrange
        Order order = Order.builder()
                .id(1L)
                .customerId(999L)
                .build();

        when(userClientPort.getUserById(999L)).thenReturn(Optional.empty());

        // Act
        traceUseCase.traceChangeStatusOrder(order, null, OrderStatus.PENDING, OrderStatus.CANCELED);

        // Assert
        verify(traceClientPort, never()).registerOrderTrace(any());
    }

}
