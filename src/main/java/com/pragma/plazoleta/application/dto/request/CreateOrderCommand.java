package com.pragma.plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderCommand {

    private Long restaurantId;

    private List<CreateOrderDetailCommand> dishes;

}