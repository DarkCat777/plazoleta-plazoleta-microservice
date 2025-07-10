package com.pragma.plazoleta.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResult<T> {

    private List<T> content;

    private Integer page;

    private Integer size;

    private Long totalElements;

    private Integer totalPages;

    public <R> PaginationResult<R> map(Function<T, R> mapper) {
        List<R> newContent = content.stream().map(mapper).toList();
        return new PaginationResult<>(newContent, page, size, totalElements, totalPages);
    }
}