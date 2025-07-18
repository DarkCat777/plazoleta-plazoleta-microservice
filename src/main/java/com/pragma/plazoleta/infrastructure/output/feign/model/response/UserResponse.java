package com.pragma.plazoleta.infrastructure.output.feign.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String firstname;

    private String lastname;

    private String identityDocument;

    private String phoneNumber;

    private LocalDate birthdate;

    private String email;

    private Long restaurantId;

    private RoleResponse role;
}
