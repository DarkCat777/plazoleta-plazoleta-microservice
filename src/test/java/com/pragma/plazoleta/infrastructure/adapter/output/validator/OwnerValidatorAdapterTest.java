package com.pragma.plazoleta.infrastructure.adapter.output.validator;

import com.pragma.plazoleta.infrastructure.adapter.output.client.UserClient;
import com.pragma.plazoleta.infrastructure.adapter.output.client.dto.RoleResponse;
import com.pragma.plazoleta.infrastructure.adapter.output.client.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OwnerValidatorAdapterTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private OwnerValidatorAdapter ownerValidatorAdapter;

    @Test
    void shouldReturnTrueWhenUserIsOwner() {
        // given
        Long userId = 1L;
        RoleResponse roleResponse = new RoleResponse(3L, "OWNER", "owner role");
        UserResponse userResponse = new UserResponse(userId, "Ana", "Torres", "ana@correo.com", "987654321", roleResponse);

        when(userClient.getUserById(userId)).thenReturn(userResponse);

        // when
        boolean result = ownerValidatorAdapter.isOwner(userId);

        // then
        assertTrue(result);
        verify(userClient).getUserById(userId);
    }

    @Test
    void shouldReturnFalseWhenUserIsNotOwner() {
        // given
        Long userId = 2L;
        RoleResponse roleResponse = new RoleResponse(1L, "CLIENT", "cliente común");
        UserResponse userResponse = new UserResponse(userId, "Luis", "Martínez", "luis@correo.com", "912345678", roleResponse);

        when(userClient.getUserById(userId)).thenReturn(userResponse);

        // when
        boolean result = ownerValidatorAdapter.isOwner(userId);

        // then
        assertFalse(result);
        verify(userClient).getUserById(userId);
    }
}
