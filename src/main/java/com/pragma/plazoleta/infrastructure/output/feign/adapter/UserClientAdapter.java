package com.pragma.plazoleta.infrastructure.output.feign.adapter;

import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.UserClientPort;
import com.pragma.plazoleta.infrastructure.exception.ExternalServiceException;
import com.pragma.plazoleta.infrastructure.output.feign.client.UserFeignClient;
import com.pragma.plazoleta.infrastructure.output.feign.mapper.UserResponseMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserClientAdapter implements UserClientPort {

    private final UserFeignClient userFeignClient;
    private final UserResponseMapper userResponseMapper;

    @Override
    public Optional<User> getUserById(Long userId) {
        try {
            return Optional.of(userResponseMapper.toDomain(userFeignClient.getUserById(userId)));
        } catch (FeignException.NotFound ex) {
            return Optional.empty();
        } catch (FeignException ex) {
            throw new ExternalServiceException("Error al comunicar con servicio de usuarios: " + ex.getMessage(), ex);
        }
    }
}