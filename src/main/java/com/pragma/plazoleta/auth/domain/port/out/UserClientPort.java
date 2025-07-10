package com.pragma.plazoleta.auth.domain.port.out;

import com.pragma.plazoleta.auth.domain.model.User;

public interface UserClientPort {

    User getUserById(Long userId);

}
