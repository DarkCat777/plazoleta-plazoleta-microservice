package com.pragma.plazoleta.auth.infrastructure.adapter.output.validator;

import com.pragma.plazoleta.auth.domain.model.Role;
import com.pragma.plazoleta.auth.domain.model.RoleName;
import com.pragma.plazoleta.auth.domain.model.User;
import com.pragma.plazoleta.auth.domain.port.out.UserClientPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRoleValidatorAdapterTest {

    @Mock
    private UserClientPort userClientPort;

    @InjectMocks
    private UserRoleValidatorAdapter ownerValidatorAdapter;

    @Test
    void shouldReturnTrueWhenUserHasOwnerRole() {
        // given
        Long userId = 1L;
        Role roleResponse = new Role(3L, RoleName.OWNER, "owner role");
        User userResponse = new User(userId, "Ana", "Torres", "987654321", "23232332", LocalDate.now(), "ana@correo.com", null, roleResponse);

        when(userClientPort.getUserById(userId)).thenReturn(userResponse);

        // when
        boolean result = ownerValidatorAdapter.hasOwnerRole(userId);

        // then
        assertTrue(result);
        verify(userClientPort).getUserById(userId);
    }

    @Test
    void shouldReturnFalseWhenUserIsNotOwner() {
        // given
        Long userId = 2L;
        Role roleResponse = new Role(3L, RoleName.CUSTOMER, "customer role");
        User userResponse = new User(userId, "Ana", "Torres", "987654321", "23232332", LocalDate.now(), "ana@correo.com", null, roleResponse);

        when(userClientPort.getUserById(userId)).thenReturn(userResponse);

        // when
        boolean result = ownerValidatorAdapter.hasOwnerRole(userId);

        // then
        assertFalse(result);
        verify(userClientPort).getUserById(userId);
    }
}
