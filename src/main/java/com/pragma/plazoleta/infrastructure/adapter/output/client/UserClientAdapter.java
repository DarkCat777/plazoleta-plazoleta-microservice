package com.pragma.plazoleta.infrastructure.adapter.output.client;

import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.port.output.UserClientPort;
import com.pragma.plazoleta.infrastructure.adapter.mapper.UserResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserClientAdapter implements UserClientPort {

    private final UserFeignClient userFeignClient;
    private final UserResponseMapper userResponseMapper;

    @Override
    public User getUserById(Long userId) {
        return userResponseMapper.toDomain(userFeignClient.getUserById(userId));
    }
}