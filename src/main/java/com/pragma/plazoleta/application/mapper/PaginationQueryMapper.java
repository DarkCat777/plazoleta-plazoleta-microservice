package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.common.PaginationQuery;
import com.pragma.plazoleta.application.dto.common.SortQuery;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaginationQueryMapper {

    default Pageable toPageable(PaginationQuery query) {
        String sortField = query.getSortBy();
        String sortDirection = query.getDirection();
        if (sortField != null && !sortField.isBlank()) {
            return PageRequest.of(
                    query.getPage(),
                    query.getSize(),
                    Sort.by(Sort.Direction.fromString(sortDirection), sortField)
            );
        } else if (sortDirection != null && !sortDirection.isBlank()) {
            return PageRequest.of(
                    query.getPage(),
                    query.getSize(),
                    Sort.by(Sort.Direction.fromString(sortDirection))
            );
        } else {
            return PageRequest.of(query.getPage(), query.getSize());
        }
    }

    default PaginationQuery toPaginationQuery(Pageable pageable) {
        Sort.Order order = pageable.getSort().stream().findFirst().orElse(null);
        String sortBy = order != null ? order.getProperty() : null;
        String direction = order != null ? order.getDirection().name() : null;

        return PaginationQuery.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                SortQuery.of(sortBy, direction)
        );
    }
}