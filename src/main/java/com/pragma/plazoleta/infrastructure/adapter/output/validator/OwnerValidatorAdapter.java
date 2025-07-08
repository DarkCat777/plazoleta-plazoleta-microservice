package com.pragma.plazoleta.infrastructure.adapter.output.validator;

import com.pragma.plazoleta.domain.model.RoleName;
import com.pragma.plazoleta.domain.port.output.OwnerValidatorPort;
import com.pragma.plazoleta.infrastructure.adapter.output.client.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnerValidatorAdapter implements OwnerValidatorPort {

    private final UserClient userClient;

    @Override
    public boolean isOwner(Long userId) {
        return userClient.getUserById(userId).getRole().getName().equals(RoleName.OWNER.name());
    }
}