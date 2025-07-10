package com.pragma.plazoleta.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser {

    private Long id;

    private String email;

    private List<String> roles;

}
