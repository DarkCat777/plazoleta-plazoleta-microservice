package com.pragma.plazoleta.application.service.impl;

import com.pragma.plazoleta.application.dto.request.CreateOrderCommand;
import com.pragma.plazoleta.application.dto.response.OrderResponse;
import com.pragma.plazoleta.application.mapper.OrderResponseMapper;
import com.pragma.plazoleta.application.service.OrderService;
import com.pragma.plazoleta.domain.usecase.OrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderUseCase orderUseCase;
    private final OrderResponseMapper orderResponseMapper;

    @Override
    public OrderResponse createOrder(Long customerId, CreateOrderCommand command) {
        return orderResponseMapper.toResponse(orderUseCase.createOrder(customerId, orderResponseMapper.toDomain(command)));
    }
}
