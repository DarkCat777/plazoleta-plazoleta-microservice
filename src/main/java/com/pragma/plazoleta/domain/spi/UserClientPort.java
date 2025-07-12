package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.User;

public interface UserClientPort {
    User getUserById(Long userId);
}
