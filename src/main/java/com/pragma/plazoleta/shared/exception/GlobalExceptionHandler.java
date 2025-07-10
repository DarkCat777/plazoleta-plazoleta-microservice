package com.pragma.plazoleta.shared.exception;

import com.pragma.plazoleta.auth.application.exception.ExternalServiceException;
import com.pragma.plazoleta.restaurant.application.exception.InvalidOwnerException;
import com.pragma.plazoleta.auth.application.exception.UserNotFoundException;
import com.pragma.plazoleta.dish.application.exception.CategoryNotFoundException;
import com.pragma.plazoleta.dish.application.exception.DishNotFoundException;
import com.pragma.plazoleta.restaurant.application.exception.RestaurantNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Usuario no encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceError(
            ExternalServiceException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_GATEWAY, "Error en servicio externo", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidOwnerException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOwner(
            InvalidOwnerException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Propietario inválido", ex.getMessage(), request);
    }

    @ExceptionHandler(DishNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDishNotFound(
            DishNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Plato no encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFound(
            CategoryNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Categoría no encontrada", ex.getMessage(), request);
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRestaurantNotFound(
            RestaurantNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Restaurante no encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status, String error, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        status.value(),
                        error,
                        message,
                        request.getRequestURI()
                )
        );
    }
}
