package com.pragma.plazoleta.auth.domain.port.out;

import com.pragma.plazoleta.auth.domain.model.AuthenticatedUser;

public interface TokenProviderPort {

    AuthenticatedUser getAuthenticatedUser(String token);

}
