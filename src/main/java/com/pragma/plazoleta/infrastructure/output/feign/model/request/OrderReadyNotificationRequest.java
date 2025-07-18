package com.pragma.plazoleta.infrastructure.output.feign.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReadyNotificationRequest {

    private Long orderId;

    private String customerPhone;

    private String securityPin;

}
