package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.infrastructure.output.jpa.model.JpaCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryEntityMapper {

    Category toDomain(JpaCategoryEntity entity);

    JpaCategoryEntity toEntity(Category domain);

}
