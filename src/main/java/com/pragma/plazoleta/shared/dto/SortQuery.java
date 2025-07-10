package com.pragma.plazoleta.shared.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SortQuery {
    @NotBlank
    private final String sortBy;
    @NotBlank
    private final String direction;

    public static SortQuery of(String sortBy, String direction) {
        return new SortQuery(sortBy, direction);
    }
}
