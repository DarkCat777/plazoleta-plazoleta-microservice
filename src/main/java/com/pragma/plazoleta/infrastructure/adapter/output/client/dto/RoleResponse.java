package com.pragma.plazoleta.infrastructure.adapter.output.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleResponse {

    private Long id;

    private String name;

    private String description;
}
