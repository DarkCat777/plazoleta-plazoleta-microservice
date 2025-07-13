package com.pragma.plazoleta.application.service;

import com.pragma.plazoleta.application.dto.request.CreateOrderCommand;
import com.pragma.plazoleta.application.dto.response.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(Long customerId, CreateOrderCommand command);

}
