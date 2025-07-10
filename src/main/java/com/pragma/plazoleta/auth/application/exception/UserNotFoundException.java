package com.pragma.plazoleta.auth.application.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Usuario no encontrado con el id: " + id);
    }

}
