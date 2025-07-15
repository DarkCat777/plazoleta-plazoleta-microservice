package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import com.pragma.plazoleta.application.mapper.PaginationQueryMapper;
import com.pragma.plazoleta.application.mapper.PaginationResultMapper;
import com.pragma.plazoleta.domain.model.Order;
import com.pragma.plazoleta.domain.model.OrderStatus;
import com.pragma.plazoleta.domain.spi.persistence.OrderRepositoryPort;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.OrderDetailEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.OrderEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.model.JpaOrderDetailEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.model.JpaOrderEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.model.pk.JpaOrderDetailId;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.JpaDishRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.JpaOrderRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.JpaRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final JpaOrderRepository jpaOrderRepository;
    private final JpaRestaurantRepository jpaRestaurantRepository;
    private final JpaDishRepository jpaDishRepository;

    private final OrderEntityMapper orderEntityMapper;
    private final OrderDetailEntityMapper orderDetailEntityMapper;
    private final PaginationQueryMapper paginationQueryMapper;
    private final PaginationResultMapper paginationResultMapper;

    @Override
    public Order save(Order order) {
        // To entity
        JpaOrderEntity jpaOrderEntity = orderEntityMapper.toEntity(order);
        // Set restaurant reference
        jpaOrderEntity.setRestaurant(jpaRestaurantRepository.getReferenceById(order.getRestaurantId()));
        // Save first to generate id
        jpaOrderRepository.save(jpaOrderEntity);
        // To entity
        List<JpaOrderDetailEntity> jpaOrderDetailEntity = order.getDishes().stream()
                .map(detail -> {
                    JpaOrderDetailEntity entity = orderDetailEntityMapper.toEntity(detail);
                    entity.setDish(jpaDishRepository.getReferenceById(detail.getDish().getId()));
                    entity.setOrder(jpaOrderEntity);
                    entity.setId(new JpaOrderDetailId(jpaOrderEntity.getId(), detail.getDish().getId()));
                    return entity;
                })
                .collect(Collectors.toList());
        // Set orderDetail reference
        jpaOrderEntity.setDishes(jpaOrderDetailEntity);

        return orderEntityMapper.toDomain(jpaOrderRepository.save(jpaOrderEntity));
    }

    @Override
    public boolean existsByCustomerIdAndStatusIn(Long customerId, List<OrderStatus> pendingStatus) {
        return jpaOrderRepository.existsByCustomerIdAndStatusIn(customerId, pendingStatus.stream().map(OrderStatus::name).toList());
    }

    @Override
    public PaginationResult<Order> findByRestaurantIdAndStatus(Long restaurantId, String status, PaginationQuery paginationQuery) {
        Pageable pageable = paginationQueryMapper.toPageable(paginationQuery);
        Page<Order> pagedOrders = jpaOrderRepository.findAllByRestaurant_IdAndStatus(restaurantId, status, pageable).map(orderEntityMapper::toDomain);
        return paginationResultMapper.toPaginatedResult(pagedOrders);
    }
}
