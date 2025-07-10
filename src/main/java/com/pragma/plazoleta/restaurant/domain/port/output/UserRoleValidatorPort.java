package com.pragma.plazoleta.restaurant.domain.port.output;

public interface UserRoleValidatorPort {

    boolean hasOwnerRole(Long userId);

}
