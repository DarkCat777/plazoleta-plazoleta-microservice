package com.pragma.plazoleta.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResult<T> {

    private List<T> content;

    private Integer page;

    private Integer size;

    private Long totalElements;

    private Integer totalPages;

    public <R> PaginatedResult<R> map(Function<T, R> mapper) {
        List<R> newContent = content.stream().map(mapper).toList();
        return new PaginatedResult<>(newContent, page, size, totalElements, totalPages);
    }
}