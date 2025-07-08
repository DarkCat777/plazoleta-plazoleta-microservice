package com.pragma.plazoleta.infrastructure.adapter.output.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-key";
    private JwtTokenProvider jwtTokenProvider;
    private Algorithm algorithm;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET);
        algorithm = Algorithm.HMAC512(SECRET);
    }

    @Test
    void shouldReturnDecodedJWTWhenTokenIsValid() {
        // given
        Instant now = Instant.now();
        String token = JWT.create()
                .withSubject("user@example.com")
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(3600)))
                .sign(algorithm);

        // when
        DecodedJWT decodedJWT = jwtTokenProvider.validateToken(token);

        // then
        assertNotNull(decodedJWT);
        assertEquals("user@example.com", decodedJWT.getSubject());
    }

    @Test
    void shouldReturnNullWhenTokenIsInvalid() {
        // given
        String invalidToken = "this.is.not.a.valid.jwt";

        // when
        DecodedJWT result = jwtTokenProvider.validateToken(invalidToken);

        // then
        assertNull(result);
    }
}
