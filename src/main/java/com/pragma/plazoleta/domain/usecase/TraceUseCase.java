package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;

public interface TraceUseCase {

    void traceChangeStatusOrder(Order order, Long employeeId, OrderStatus prevStatus, OrderStatus nextStatus);

}
