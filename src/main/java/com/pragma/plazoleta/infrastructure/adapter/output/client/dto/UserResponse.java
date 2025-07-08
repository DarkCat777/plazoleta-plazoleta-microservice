package com.pragma.plazoleta.infrastructure.adapter.output.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String firstname;

    private String lastname;

    private String email;

    private String phoneNumber;

    private RoleResponse role;
}
