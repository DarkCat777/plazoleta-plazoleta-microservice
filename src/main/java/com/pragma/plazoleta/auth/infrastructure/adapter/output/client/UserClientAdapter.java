package com.pragma.plazoleta.auth.infrastructure.adapter.output.client;

import com.pragma.plazoleta.auth.application.exception.ExternalServiceException;
import com.pragma.plazoleta.auth.application.exception.UserNotFoundException;
import com.pragma.plazoleta.auth.domain.model.User;
import com.pragma.plazoleta.auth.domain.port.out.UserClientPort;
import com.pragma.plazoleta.auth.infrastructure.adapter.mapper.UserResponseMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserClientAdapter implements UserClientPort {

    private final UserFeignClient userFeignClient;
    private final UserResponseMapper userResponseMapper;

    @Override
    public User getUserById(Long userId) {
        try {
            return userResponseMapper.toDomain(userFeignClient.getUserById(userId));
        } catch (FeignException.NotFound ex) {
            throw new UserNotFoundException(userId);
        } catch (FeignException ex) {
            throw new ExternalServiceException("Error al comunicar con servicio de usuarios: " + ex.getMessage(), ex);
        }
    }
}