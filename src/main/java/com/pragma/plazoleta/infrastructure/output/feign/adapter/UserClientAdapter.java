package com.pragma.plazoleta.infrastructure.output.feign.adapter;

import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.UserClientPort;
import com.pragma.plazoleta.infrastructure.exception.ExternalServiceException;
import com.pragma.plazoleta.infrastructure.exception.UserNotFoundException;
import com.pragma.plazoleta.infrastructure.output.feign.client.UserFeignClient;
import com.pragma.plazoleta.infrastructure.output.feign.mapper.UserResponseMapper;
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