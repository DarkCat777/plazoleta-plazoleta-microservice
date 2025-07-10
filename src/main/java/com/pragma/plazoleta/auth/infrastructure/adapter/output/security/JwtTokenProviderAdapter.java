package com.pragma.plazoleta.auth.infrastructure.adapter.output.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.pragma.plazoleta.auth.domain.model.AuthenticatedUser;
import com.pragma.plazoleta.auth.domain.port.out.TokenProviderPort;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private final JWTVerifier verifier;

    public JwtTokenProviderAdapter(@Value("${jwt.secret}") final String secret) {
        this.verifier = JWT.require(Algorithm.HMAC512(secret)).build();
    }

    @Override
    public AuthenticatedUser getAuthenticatedUser(final String token) {
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
            return AuthenticatedUser.builder()
                    .id(decodedJWT.getClaim("id").asLong())
                    .email(decodedJWT.getSubject())
                    .roles(roles)
                    .build();
        } catch (final JWTVerificationException verificationEx) {
            log.warn("Token invalid: {}", verificationEx.getMessage());
            return null;
        }
    }
}
