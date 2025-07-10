package com.pragma.plazoleta.application.dto.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationQuery {

    private final Integer page;

    private final Integer size;

    private String sortBy;

    private String direction;

    public static PaginationQuery of(Integer page, Integer size) {
        return new PaginationQuery(page, size);
    }

    public static PaginationQuery of(Integer page, Integer size, SortQuery sortQuery) {
        return new PaginationQuery(page, size, sortQuery.getSortBy(), sortQuery.getDirection());
    }
}
