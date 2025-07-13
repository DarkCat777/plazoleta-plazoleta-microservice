package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant {

    private Long id;

    private String name;

    private String address;

    private String phone;

    private String logoUrl;

    private String nit;

    private Long ownerId;
}