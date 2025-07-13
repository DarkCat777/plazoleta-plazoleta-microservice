package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private Long id;

    private Long customerId;

    private Long chefId;

    private OrderStatus status;

    private LocalDateTime createdAt;

    private Long restaurantId;

    private List<OrderDetail> dishes;
}
