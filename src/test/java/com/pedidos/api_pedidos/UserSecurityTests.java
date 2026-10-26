package com.pedidos.api_pedidos;

import com.pedidos.api_pedidos.domain.entity.UserEntity;
import com.pedidos.api_pedidos.domain.enums.UserRole;
import com.pedidos.api_pedidos.dto.auth.LoginRequest;
import com.pedidos.api_pedidos.dto.auth.RegisterRequest;
import com.pedidos.api_pedidos.exception.UnauthorizedException;
import com.pedidos.api_pedidos.repository.UserRepository;
import com.pedidos.api_pedidos.security.JwtUtil;
import com.pedidos.api_pedidos.service.UserService;
import com.pedidos.api_pedidos.service.AuditService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserSecurityTests {
    private final UserRepository users = mock(UserRepository.class);
    private final PasswordEncoder passwords = mock(PasswordEncoder.class);
    private final JwtUtil jwt = mock(JwtUtil.class);
    private final AuditService audit = mock(AuditService.class);
    private final UserService service = new UserService(users, passwords, jwt, audit);

    @AfterEach
    void clearContext() { SecurityContextHolder.clearContext(); }

    @Test
    void firstAccountIsAdminEvenWhenClientRequestsAnotherRole() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Owner");
        request.setEmail("owner@example.com");
        request.setPassword("secret123");
        request.setRole("WAITER");
        when(users.countByDeletedAtIsNull()).thenReturn(0L);
        when(passwords.encode("secret123")).thenReturn("hashed");
        when(users.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.register(request);

        verify(users).save(argThat(user -> user.getRole() == UserRole.ADMIN
                && user.getPasswordHash().equals("hashed")));
    }

    @Test
    void publicRegistrationClosesAfterFirstAccount() {
        when(users.countByDeletedAtIsNull()).thenReturn(1L);
        assertThrows(AccessDeniedException.class, () -> service.register(new RegisterRequest()));
        verify(users, never()).save(any());
    }

    @Test
    void inactiveAccountCannotLogIn() {
        UserEntity user = new UserEntity("Staff", "staff@example.com", "hashed", UserRole.WAITER);
        user.setActive(false);
        LoginRequest request = new LoginRequest();
        request.setEmail("staff@example.com");
        request.setPassword("secret123");
        when(users.findByEmailAndDeletedAtIsNull("staff@example.com")).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> service.login(request));
        verify(jwt, never()).generateUserToken(any());
    }
}
