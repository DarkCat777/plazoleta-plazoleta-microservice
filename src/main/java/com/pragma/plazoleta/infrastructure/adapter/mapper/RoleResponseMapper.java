package com.pragma.plazoleta.infrastructure.adapter.mapper;

import com.pragma.plazoleta.domain.model.Role;
import com.pragma.plazoleta.infrastructure.adapter.output.client.dto.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleResponseMapper extends BaseResponseMapper<Role, RoleResponse> {
}
