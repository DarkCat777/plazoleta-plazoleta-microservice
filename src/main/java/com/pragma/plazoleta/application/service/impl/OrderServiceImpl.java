package com.pragma.plazoleta.application.service.impl;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.application.dto.request.CreateOrderCommand;
import com.pragma.plazoleta.application.dto.request.OrderByStatusQuery;
import com.pragma.plazoleta.application.dto.request.OrderSecurityPinQuery;
import com.pragma.plazoleta.application.dto.response.OrderResponse;
import com.pragma.plazoleta.application.mapper.OrderResponseMapper;
import com.pragma.plazoleta.application.mapper.PaginationQueryMapper;
import com.pragma.plazoleta.application.mapper.PaginationResultMapper;
import com.pragma.plazoleta.application.service.OrderService;
import com.pragma.plazoleta.domain.usecase.OrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderUseCase orderUseCase;
    private final OrderResponseMapper orderResponseMapper;
    private final PaginationQueryMapper paginationQueryMapper;
    private final PaginationResultMapper paginationResultMapper;

    @Override
    public OrderResponse createOrder(Long customerId, CreateOrderCommand command) {
        return orderResponseMapper.toResponse(orderUseCase.createOrder(customerId, orderResponseMapper.toDomain(command)));
    }

    @Override
    public Page<OrderResponse> getOrdersByStatusForEmployee(Long employeeId, OrderByStatusQuery query, Pageable pageable) {
        PaginationQuery paginationQuery = paginationQueryMapper.toPaginationQuery(pageable);
        PaginationResult<OrderResponse> paginationResult = orderUseCase.findOrdersByStatusForEmployee(employeeId, query.getStatus(), paginationQuery)
                .map(orderResponseMapper::toResponse);
        return paginationResultMapper.toPage(paginationResult);
    }

    @Override
    public OrderResponse assignOrderToEmployee(Long orderId, Long employeeId) {
        return orderResponseMapper.toResponse(orderUseCase.assignOrderToEmployee(orderId, employeeId));
    }

    @Override
    public OrderResponse markOrderAsReady(Long orderId, Long employeeId) {
        return orderResponseMapper.toResponse(orderUseCase.markOrderAsReady(orderId, employeeId));
    }

    @Override
    public OrderResponse markOrderAsDelivered(Long orderId, Long employeeId, OrderSecurityPinQuery query) {
        return orderResponseMapper.toResponse(orderUseCase.markOrderAsDelivered(orderId, employeeId, query.getSecurityPin()));
    }

    @Override
    public OrderResponse markOrderAsCancelled(Long orderId, Long customerId) {
        return orderResponseMapper.toResponse(orderUseCase.markOrderAsCancelled(orderId, customerId));
    }
}
