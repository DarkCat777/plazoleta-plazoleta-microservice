package com.pragma.plazoleta.auth.infrastructure.adapter.mapper;

import com.pragma.plazoleta.auth.domain.model.User;
import com.pragma.plazoleta.shared.mapper.BaseResponseMapper;
import com.pragma.plazoleta.auth.infrastructure.adapter.output.client.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {RoleResponseMapper.class})
public interface UserResponseMapper extends BaseResponseMapper<User, UserResponse> {

    @Override
    @Mapping(target = "password", ignore = true)
    User toDomain(UserResponse response);
}
