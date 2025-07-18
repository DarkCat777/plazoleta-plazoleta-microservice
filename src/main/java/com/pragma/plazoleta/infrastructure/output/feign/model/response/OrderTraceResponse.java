package com.pragma.plazoleta.infrastructure.output.feign.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderTraceResponse {

    private String id;

    private Long orderId;

    private Long customerId;

    private String customerEmail;

    private LocalDateTime date;

    private String previousStatus;

    private String newStatus;

    private Long employeeId;

    private String employeeEmail;
}
