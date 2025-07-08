package com.pragma.plazoleta.domain.port.output;

import com.auth0.jwt.interfaces.DecodedJWT;

public interface TokenProviderPort {
    DecodedJWT validateToken(String token);
}
