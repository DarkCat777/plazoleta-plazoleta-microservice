package com.pragma.plazoleta.dish.infrastructure.adapter.mapper;

import com.pragma.plazoleta.dish.domain.model.Category;
import com.pragma.plazoleta.dish.infrastructure.adapter.output.model.JpaCategoryEntity;
import com.pragma.plazoleta.shared.mapper.BaseEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryEntityMapper extends BaseEntityMapper<Category, JpaCategoryEntity> {
}
