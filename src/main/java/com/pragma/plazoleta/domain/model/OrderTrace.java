package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTrace {

    private String id;

    private Long orderId;

    private User customer;

    private LocalDateTime date;

    private OrderStatus previousStatus;

    private OrderStatus newStatus;

    private User employee;

}