package com.pragma.plazoleta.application.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pragma.plazoleta.domain.validation.errors.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationErrorResponse {

    private Integer index;
    private String field;
    private Object rejectedValue;
    @Setter
    private List<String> messages;
    private List<ValidationErrorResponse> subErrors;

    private ValidationErrorResponse(String field, Object rejectedValue) {
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    private ValidationErrorResponse(String field, List<ValidationErrorResponse> subErrors) {
        this.field = field;
        this.subErrors = subErrors;
    }

    private ValidationErrorResponse(Integer index, List<ValidationErrorResponse> subErrors) {
        this.index = index;
        this.subErrors = subErrors;
    }

    public ValidationErrorResponse(String field, Object rejectedValue, List<String> messages) {
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.messages = new ArrayList<>(messages);
    }

    public static ValidationErrorResponse from(ValidationError error) {
        if (error instanceof WithMessage withMessage && error instanceof WithField withField) {
            return new ValidationErrorResponse(
                    withField.getField(),
                    error.getRejectedValue(),
                    Collections.singletonList(withMessage.getMessage())
            );
        }
        if (error instanceof WithChildrenError withChildrenError) {
            // Paso 1: Transformar hijos recursivamente
            List<ValidationErrorResponse> reconstructedChildren = withChildrenError.getChildren()
                    .stream()
                    .map(ValidationErrorResponse::from)
                    .filter(Objects::nonNull)
                    .toList();

            // Paso 2: Agrupar errores simples por campo en este nivel
            Map<String, ValidationErrorResponse> fieldToErrorMap = new HashMap<>();
            List<ValidationErrorResponse> groupedChildren = new ArrayList<>();

            for (ValidationErrorResponse child : reconstructedChildren) {
                // Solo agrupa errores simples (sin sub-errores) con campo definido
                if (child.getSubErrors() == null && child.getField() != null) {
                    String field = child.getField();
                    if (fieldToErrorMap.containsKey(field)) {
                        // Fusionar mensajes si ya existe un error para este campo
                        ValidationErrorResponse existing = fieldToErrorMap.get(field);
                        List<String> mergedMessages = new ArrayList<>(existing.getMessages());
                        mergedMessages.addAll(child.getMessages());
                        existing.setMessages(mergedMessages);
                    } else {
                        // Primer error para este campo
                        fieldToErrorMap.put(field, child);
                        groupedChildren.add(child);
                    }
                } else {
                    // Conservar errores compuestos o sin campo tal cual
                    groupedChildren.add(child);
                }
            }

            // Paso 3: Construir respuesta con hijos agrupados
            if (error instanceof WithField withField) {
                return new ValidationErrorResponse(withField.getField(), groupedChildren);
            }
            if (error instanceof WithIndex withIndex) {
                return new ValidationErrorResponse(withIndex.getIndex(), groupedChildren);
            }
        }
        return null;
    }
}
