package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.request.CreateOrderCommand;
import com.pragma.plazoleta.application.dto.request.OrderByStatusQuery;
import com.pragma.plazoleta.application.dto.request.OrderSecurityPinQuery;
import com.pragma.plazoleta.application.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse createOrder(Long customerId, CreateOrderCommand command);

    Page<OrderResponse> getOrdersByStatusForEmployee(Long employeeId, OrderByStatusQuery query, Pageable pageable);

    OrderResponse assignOrderToEmployee(Long orderId, Long employeeId);

    OrderResponse markOrderAsReady(Long orderId, Long employeeId);

    OrderResponse markOrderAsDelivered(Long orderId, Long employeeId, OrderSecurityPinQuery query);
}
