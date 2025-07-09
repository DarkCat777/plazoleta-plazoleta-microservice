package com.pragma.plazoleta.infrastructure.adapter.mapper;


import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.infrastructure.adapter.input.dto.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryResponseMapper extends BaseResponseMapper<Category, CategoryResponse> {
}
