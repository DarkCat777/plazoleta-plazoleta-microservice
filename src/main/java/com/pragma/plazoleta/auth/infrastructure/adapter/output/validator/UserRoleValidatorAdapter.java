package com.pragma.plazoleta.auth.infrastructure.adapter.output.validator;

import com.pragma.plazoleta.auth.domain.model.RoleName;
import com.pragma.plazoleta.auth.domain.port.out.UserClientPort;
import com.pragma.plazoleta.restaurant.domain.port.output.UserRoleValidatorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleValidatorAdapter implements UserRoleValidatorPort {

    private final UserClientPort userClientPort;

    @Override
    public boolean hasOwnerRole(Long userId) {
        return userClientPort.getUserById(userId).getRole().getName().equals(RoleName.OWNER);
    }
}