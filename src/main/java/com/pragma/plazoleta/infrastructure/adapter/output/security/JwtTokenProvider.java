package com.pragma.plazoleta.infrastructure.adapter.output.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.pragma.plazoleta.domain.port.output.TokenProviderPort;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class JwtTokenProvider implements TokenProviderPort {

    private final JWTVerifier verifier;

    public JwtTokenProvider(@Value("${jwt.secret}") final String secret) {
        this.verifier = JWT.require(Algorithm.HMAC512(secret)).build();
    }

    @Override
    public DecodedJWT validateToken(final String token) {
        try {
            return verifier.verify(token);
        } catch (final JWTVerificationException verificationEx) {
            log.warn("Token invalid: {}", verificationEx.getMessage());
            return null;
        }
    }
}
