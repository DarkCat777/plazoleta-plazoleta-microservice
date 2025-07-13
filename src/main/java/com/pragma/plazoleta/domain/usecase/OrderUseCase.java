package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.model.Order;

public interface OrderUseCase {
    Order createOrder(Long customerId, Order order);
}
