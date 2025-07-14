package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.User;

import java.util.Optional;

public interface UserClientPort {
    Optional<User> getUserById(Long userId);
}
