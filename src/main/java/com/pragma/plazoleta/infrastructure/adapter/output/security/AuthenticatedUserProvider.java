package com.pragma.plazoleta.infrastructure.adapter.output.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.pragma.plazoleta.domain.port.output.UserPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider implements UserPort {
    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        DecodedJWT decodeJwt = (DecodedJWT) authentication.getDetails();
        if (decodeJwt != null && decodeJwt.getClaim("id").asLong() != null) {
            return decodeJwt.getClaim("id").asLong();
        }

        throw new IllegalStateException("ID de usuario no encontrado en el token");
    }
}
