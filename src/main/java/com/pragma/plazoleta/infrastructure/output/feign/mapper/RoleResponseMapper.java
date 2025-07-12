package com.pragma.plazoleta.infrastructure.output.feign.mapper;

import com.pragma.plazoleta.domain.model.Role;
import com.pragma.plazoleta.infrastructure.output.feign.model.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleResponseMapper {

    Role toDomain(RoleResponse response);

}
