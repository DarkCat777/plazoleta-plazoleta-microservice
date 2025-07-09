package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    private String firstname;

    private String lastname;

    private String identityDocument;

    private String phoneNumber;

    private LocalDate birthdate;

    private String email;

    private String password;

    private Role role;
}