package com.pragma.plazoleta.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRestaurantCommand {

    private String name;

    private String address;

    private String phone;

    private String logoUrl;

    private String nit;

    private Long ownerId;

}
