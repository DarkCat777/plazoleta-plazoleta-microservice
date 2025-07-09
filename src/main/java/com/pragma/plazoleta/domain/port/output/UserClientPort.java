package com.pragma.plazoleta.domain.port.output;

import com.pragma.plazoleta.domain.model.User;

public interface UserClientPort {
    User getUserById(Long userId);
}
