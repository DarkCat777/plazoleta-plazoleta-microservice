package com.pragma.plazoleta.application.dto.common;

import lombok.Data;

import java.util.List;
import java.util.function.Function;

@Data
public class PaginationResult<T> {

    private List<T> content;

    private Integer page;

    private Integer size;

    private Long totalElements;

    private Integer totalPages;

    public PaginationResult(List<T> content, PaginationQuery query, Long totalElements) {
        this.content = content;
        this.totalElements = totalElements;
        this.page = query.getPage();
        this.size = query.getSize();
        this.totalPages = (int) Math.ceil((double) totalElements / query.getSize());
    }

    public <R> PaginationResult<R> map(Function<T, R> mapper) {
        List<R> newContent = content.stream().map(mapper).toList();
        return new PaginationResult<>(newContent, PaginationQuery.of(page, size), totalElements);
    }
}