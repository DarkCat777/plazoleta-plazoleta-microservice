package com.pragma.plazoleta.domain.model;

import lombok.*;

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