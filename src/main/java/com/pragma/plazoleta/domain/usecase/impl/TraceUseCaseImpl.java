package com.pragma.plazoleta.domain.usecase.impl;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.model.OrderTrace;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.TraceClientPort;
import com.pragma.plazoleta.domain.spi.UserClientPort;
import com.pragma.plazoleta.domain.usecase.TraceUseCase;
import com.pragma.plazoleta.infrastructure.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class TraceUseCaseImpl implements TraceUseCase {

    private final UserClientPort userClientPort;
    private final TraceClientPort traceClientPort;

    @Override
    public void traceChangeStatusOrder(Order order, OrderStatus prevStatus, OrderStatus nextStatus) {
        try {
            User customer = userClientPort.getUserById(order.getCustomerId())
                    .orElseThrow(() -> new UserNotFoundException(order.getCustomerId()));

            User employee = userClientPort.getUserById(order.getCustomerId())
                    .orElseThrow(() -> new UserNotFoundException(order.getCustomerId()));

            OrderTrace orderTrace = OrderTrace.builder()
                    .orderId(order.getId())
                    .previousStatus(prevStatus)
                    .newStatus(nextStatus)
                    .customer(User.builder()
                            .id(customer.getId())
                            .email(customer.getEmail())
                            .build())
                    .employee(User.builder()
                            .id(employee.getId())
                            .email(employee.getEmail())
                            .build())
                    .build();

            traceClientPort.registerOrderTrace(orderTrace);
        } catch (UserNotFoundException e) {
            log.error("Error no se encontró los datos del cliente con Id {}: {}", order.getCustomerId(), e.getMessage());
        } catch (Exception e) {
            log.error("Error registrando log del pedido {}: {}", order.getId(), e.getMessage());
        }
    }
}
