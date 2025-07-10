package com.pragma.plazoleta.auth.infrastructure.adapter.mapper;

import com.pragma.plazoleta.auth.domain.model.Role;
import com.pragma.plazoleta.shared.mapper.BaseResponseMapper;
import com.pragma.plazoleta.auth.infrastructure.adapter.output.client.dto.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleResponseMapper extends BaseResponseMapper<Role, RoleResponse> {
}
