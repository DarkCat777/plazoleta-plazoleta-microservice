package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.AuthenticatedUser;

public interface TokenProviderPort {
    AuthenticatedUser decodeToken(final String token);
}
