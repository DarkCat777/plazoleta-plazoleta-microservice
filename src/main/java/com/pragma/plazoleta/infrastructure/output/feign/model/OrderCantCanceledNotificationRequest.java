package com.pragma.plazoleta.infrastructure.output.feign.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCantCanceledNotificationRequest {

    private Long orderId;

    private String orderStatus;

    private String customerPhone;

}
