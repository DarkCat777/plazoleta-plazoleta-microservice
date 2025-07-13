package com.pragma.plazoleta.domain.exception;

public class BusinessLogicException extends RuntimeException {

    public static final String DISHES_ARE_NOT_SAME_RESTAURANT = "Todos los platos deben ser del mismo restaurante.";
    public static final String PENDING_ORDER = "El cliente ya tiene un pedido en proceso.";
    public static final String DISH_NOT_EXIST = "Uno o más platos no existen.";
    public static final String RESTAURANT_NOT_EXIST = "El restaurante no existe.";

    public BusinessLogicException(String message) {
        super(message);
    }
}
