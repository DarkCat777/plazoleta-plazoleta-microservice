package com.pragma.plazoleta.infrastructure.output.feign.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderNotificationRequest {

    private Long orderId;

    private String customerPhone;

    private String securityPin;

}
