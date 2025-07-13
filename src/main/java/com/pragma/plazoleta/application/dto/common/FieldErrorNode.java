package com.pragma.plazoleta.application.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class FieldErrorNode {
    private List<String> message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, FieldErrorNode> subField;

    public FieldErrorNode() {
        this.message = new ArrayList<>();
        this.subField = new HashMap<>();
    }

    public void addMessage(String msg) {
        this.message.add(msg);
    }

}

