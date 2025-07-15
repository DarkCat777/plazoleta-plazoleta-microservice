package com.pragma.plazoleta.domain.spi.persistence;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {

    Order save(Order order);

    boolean existsByCustomerIdAndStatusIn(Long customerId, List<OrderStatus> pendingStatus);

    PaginationResult<Order> findByRestaurantIdAndStatus(Long restaurantId, String status, PaginationQuery paginationQuery);

    Optional<Order> findById(Long orderId);
}
