package com.pragma.plazoleta.infrastructure.adapter.mapper;

import com.pragma.plazoleta.application.dto.common.PaginationResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaginationResultMapper {

    default <T> PaginationResult<T> toPaginatedResult(Page<T> page) {
        return new PaginationResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

}
