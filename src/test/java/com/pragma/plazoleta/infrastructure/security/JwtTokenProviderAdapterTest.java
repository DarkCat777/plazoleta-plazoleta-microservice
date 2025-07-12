package com.pragma.plazoleta.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.pragma.plazoleta.domain.model.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderAdapterTest {

    private static final String SECRET = "test-secret-key";
    private JwtTokenProviderAdapter jwtTokenProviderAdapter;
    private Algorithm algorithm;

    @BeforeEach
    void setUp() {
        jwtTokenProviderAdapter = new JwtTokenProviderAdapter(SECRET);
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
        AuthenticatedUser authenticatedUser = jwtTokenProviderAdapter.decodeToken(token);

        // then
        assertNotNull(authenticatedUser);
        assertEquals("user@example.com", authenticatedUser.getEmail());
    }

    @Test
    void shouldReturnNullWhenTokenIsInvalid() {
        // given
        String invalidToken = "this.is.not.a.valid.jwt";

        // when
        AuthenticatedUser authenticatedUser = jwtTokenProviderAdapter.decodeToken(invalidToken);

        // then
        assertNull(authenticatedUser);
    }
}
