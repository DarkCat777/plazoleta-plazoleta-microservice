package com.pragma.plazoleta.infrastructure.adapter.output.security;

import com.pragma.plazoleta.domain.port.output.UserPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthenticatedUserProvider implements UserPort {
    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object details = authentication.getDetails();
        if (details instanceof Map<?, ?> map) {
            return ((Number) map.get("id")).longValue();
        }

        throw new IllegalStateException("ID de usuario no encontrado en el token");
    }
}
