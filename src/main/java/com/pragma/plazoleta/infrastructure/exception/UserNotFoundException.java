package com.pragma.plazoleta.infrastructure.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Usuario no encontrado con el id: " + id);
    }

}
