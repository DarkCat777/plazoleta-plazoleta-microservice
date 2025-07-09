package com.pragma.plazoleta.infrastructure.adapter.mapper;

import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.infrastructure.adapter.output.client.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {RoleResponseMapper.class})
public interface UserResponseMapper extends BaseResponseMapper<User, UserResponse> {

    @Override
    @Mapping(target = "password", ignore = true)
    User toDomain(UserResponse response);
}
