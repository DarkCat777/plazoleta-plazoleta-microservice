package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.PaginationResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaginationResultMapper {

    default <T> PaginationResult<T> toPaginatedResult(Page<T> page) {
        return new PaginationResult<>(
                page.getContent(),
                PaginationQuery.of(page.getNumber(), page.getSize()),
                page.getTotalElements()
        );
    }

    default <T> Page<T> toPage(PaginationResult<T> page) {
        return new PageImpl<>(
                page.getContent(),
                PageRequest.of(page.getPage(), page.getSize()),
                page.getTotalElements()
        );
    }

}
