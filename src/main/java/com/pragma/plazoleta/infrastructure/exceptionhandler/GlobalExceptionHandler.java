package com.pragma.plazoleta.infrastructure.exceptionhandler;

import com.pragma.plazoleta.application.dto.common.ErrorResponse;
import com.pragma.plazoleta.application.dto.common.FieldErrorNode;
import com.pragma.plazoleta.domain.validation.CompositeValidationError;
import com.pragma.plazoleta.domain.validation.FieldValidationError;
import com.pragma.plazoleta.domain.validation.exception.ValidationException;
import com.pragma.plazoleta.domain.exception.CategoryNotFoundException;
import com.pragma.plazoleta.domain.exception.DishNotFoundException;
import com.pragma.plazoleta.domain.exception.InvalidOwnerException;
import com.pragma.plazoleta.domain.exception.RestaurantNotFoundException;
import com.pragma.plazoleta.domain.exception.BusinessLogicException;
import com.pragma.plazoleta.infrastructure.exception.ExternalServiceException;
import com.pragma.plazoleta.infrastructure.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


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

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(
            AuthorizationDeniedException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                "Acceso no autorizado",
                request
        );
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(
            BusinessLogicException ex,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "Conflict",
                ex.getMessage(),
                request
        );
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleDomainValidationException(
            ValidationException ex,
            HttpServletRequest request
    ) {
        Map<String, FieldErrorNode> fieldErrors = new HashMap<>();
        for (FieldValidationError error : ex.getErrors()) {
            buildFieldErrorTree(fieldErrors, error);
        }
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validación de dominio",
                null,
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", ex.getMessage(), request);
    }

    private void buildFieldErrorTree(Map<String, FieldErrorNode> map, FieldValidationError error) {
        String[] path = error.getField().split("\\.");
        insertError(map, path, 0, error);
    }

    private void insertError(Map<String, FieldErrorNode> current, String[] path, int index, FieldValidationError error) {
        String part = path[index];
        FieldErrorNode node = current.computeIfAbsent(part, k -> new FieldErrorNode());
        if (index == path.length - 1) {
            if (error instanceof CompositeValidationError composite) {
                node.addMessage(error.getMessage() != null && !error.getMessage().isEmpty() ? error.getMessage() : "Error de validación.");
                for (FieldValidationError child : composite.getChildren()) {
                    insertError(node.getSubField(), new String[]{child.getField()}, 0, child);
                }
            } else {
                node.addMessage(error.getMessage());
            }
        } else {
            insertError(node.getSubField(), path, index + 1, error);
        }
    }


    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status, String error, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        status.value(),
                        error,
                        message,
                        request.getRequestURI(),
                        null
                )
        );
    }
}
