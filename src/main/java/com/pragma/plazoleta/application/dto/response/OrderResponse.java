package com.pragma.plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long id;

    private Long customerId;

    private Long chefId;

    private String status;

    private LocalDateTime createdAt;

    private Long restaurantId;

    private List<OrderDetailResponse> dishes;

}
