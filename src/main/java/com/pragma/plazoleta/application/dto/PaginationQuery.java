package com.pragma.plazoleta.application.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PaginationQuery {
    @PositiveOrZero
    private Integer page;
    @Positive
    private Integer size;

    private PaginationQuery(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public static PaginationQuery of(Integer page, Integer size) {
        return new PaginationQuery(page, size);
    }
}
