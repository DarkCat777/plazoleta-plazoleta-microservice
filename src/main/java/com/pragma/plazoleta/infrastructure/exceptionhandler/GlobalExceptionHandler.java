package com.pragma.plazoleta.infrastructure.exceptionhandler;

import com.pragma.plazoleta.application.dto.common.ErrorResponse;
import com.pragma.plazoleta.domain.exception.*;
import com.pragma.plazoleta.domain.validation.CompositeValidationError;
import com.pragma.plazoleta.domain.validation.FieldValidationError;
import com.pragma.plazoleta.domain.validation.ItemFieldValidationError;
import com.pragma.plazoleta.domain.validation.exception.ValidationException;
import com.pragma.plazoleta.infrastructure.exception.ExternalServiceException;
import com.pragma.plazoleta.infrastructure.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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
            AuthorizationDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", "Acceso no autorizado", request);
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(
            BusinessLogicException ex, HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error(ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", ex.getMessage(), request);
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        Map<String, Object> fieldErrors = buildFieldErrors(ex.getErrors());

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

    private Map<String, Object> buildFieldErrors(List<FieldValidationError> errors) {
        Map<String, Object> fieldErrors = new LinkedHashMap<>();
        for (FieldValidationError error : errors) {
            String field = error.getField();
            Map<String, Object> fieldEntry = (Map<String, Object>) fieldErrors.computeIfAbsent(field, f -> new LinkedHashMap<>());
            if (error instanceof CompositeValidationError composite) {
                appendMessageToField(fieldEntry, "Error de validación.");
                List<FieldValidationError> children = composite.getChildren();
                boolean allItemsIndexed = children.stream().allMatch(e -> e instanceof ItemFieldValidationError);
                if (allItemsIndexed) {
                    fieldEntry.put("indexField", groupErrorsByIndex(children));
                } else {
                    fieldEntry.put("subField", buildFieldErrors(children));
                }
            } else {
                appendMessageToField(fieldEntry, error.getMessage());
            }
        }

        return fieldErrors;
    }

    private List<Map<String, Object>> groupErrorsByIndex(List<FieldValidationError> children) {
        Map<Integer, Map<String, Object>> indexFieldMap = new LinkedHashMap<>();

        for (FieldValidationError err : children) {
            ItemFieldValidationError itemError = (ItemFieldValidationError) err;
            int index = itemError.getIndex();

            Map<String, Object> indexEntry = indexFieldMap.computeIfAbsent(index, idx -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("index", idx);
                map.put("field", new LinkedHashMap<String, Object>());
                return map;
            });

            Map<String, Object> fieldErrors = (Map<String, Object>) indexEntry.get("field");
            fieldErrors.computeIfAbsent(itemError.getField(), k -> new LinkedHashMap<>());
            appendMessageToField((Map<String, Object>) fieldErrors.get(itemError.getField()), itemError.getMessage());
        }

        return new ArrayList<>(indexFieldMap.values());
    }

    private void appendMessageToField(Map<String, Object> fieldMap, String message) {
        fieldMap.computeIfAbsent("message", k -> new ArrayList<>());
        ((List<String>) fieldMap.get("message")).add(message);
    }
}
