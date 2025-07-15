package com.pragma.plazoleta.domain.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super("No existe el pedido con el ID:" + orderId);
    }
}
