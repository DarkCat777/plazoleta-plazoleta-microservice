package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCantCanceledNotification {
    private Long orderId;
    private OrderStatus orderStatus;
    private String customerPhone;
}
