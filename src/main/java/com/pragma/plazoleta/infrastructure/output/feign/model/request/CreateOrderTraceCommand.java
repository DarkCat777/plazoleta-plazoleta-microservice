package com.pragma.plazoleta.infrastructure.output.feign.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderTraceCommand {

    private Long orderId;

    private Long customerId;

    private String customerEmail;

    private String previousStatus;

    private String newStatus;

    private Long employeeId;

    private String employeeEmail;
}

