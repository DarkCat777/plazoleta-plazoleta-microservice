package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.domain.model.Order;

public interface OrderUseCase {
    Order createOrder(Long customerId, Order order);

    PaginationResult<Order> findOrdersByStatusForEmployee(Long employeeId, String status, PaginationQuery paginationQuery);

    Order assignOrderToEmployee(Long orderId, Long employeeId);

    Order markOrderAsReady(Long orderId, Long employeeId);
}
