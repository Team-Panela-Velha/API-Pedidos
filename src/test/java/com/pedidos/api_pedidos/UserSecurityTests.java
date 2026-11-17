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

import com.pedidos.api_pedidos.dto.user.UserRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
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

    // root = oldest active ADMIN (id 1 here); the caller is chosen per test
    private UserEntity user(long id, UserRole role) {
        UserEntity u = new UserEntity("U" + id, "u" + id + "@example.com", "hashed", role);
        u.setId(id);
        when(users.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(u));
        return u;
    }

    private void loggedAs(long id) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "caller", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        when(audit.currentUserId()).thenReturn(id);
        UserEntity root = user(1, UserRole.ADMIN);
        when(users.findFirstByRoleAndDeletedAtIsNullOrderByIdAsc(UserRole.ADMIN)).thenReturn(Optional.of(root));
        when(users.countByDeletedAtIsNull()).thenReturn(2L);
        when(users.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private RegisterRequest newUser(String role) {
        RegisterRequest request = new RegisterRequest();
        request.setName("Novo");
        request.setEmail("novo@example.com");
        request.setPassword("secret123");
        request.setRole(role);
        return request;
    }

    @Test
    void onlyRootCreatesAdmins() {
        loggedAs(2);
        assertThrows(AccessDeniedException.class, () -> service.register(newUser("ADMIN")));
        verify(users, never()).save(any());
    }

    @Test
    void rootCreatesAdminAndAdminCreatesWaiter() {
        loggedAs(1);
        assertEquals("ADMIN", service.register(newUser("ADMIN")).getRole());
        when(audit.currentUserId()).thenReturn(2L);
        assertEquals("WAITER", service.register(newUser("WAITER")).getRole());
    }

    @Test
    void rootCannotBeDeletedDemotedOrDeactivated() {
        loggedAs(1);
        assertThrows(AccessDeniedException.class, () -> service.delete(1L));
        UserRequest demote = new UserRequest();
        demote.setName("Root");
        demote.setEmail("u1@example.com");
        demote.setRole("WAITER");
        assertThrows(AccessDeniedException.class, () -> service.update(1L, demote));
        UserRequest deactivate = new UserRequest();
        deactivate.setName("Root");
        deactivate.setEmail("u1@example.com");
        deactivate.setActive(false);
        assertThrows(AccessDeniedException.class, () -> service.update(1L, deactivate));
        verify(users, never()).save(any());
    }

    @Test
    void adminCannotTouchOtherAdminsButManagesStaff() {
        loggedAs(2);
        user(3, UserRole.ADMIN);
        user(4, UserRole.WAITER);
        assertThrows(AccessDeniedException.class, () -> service.delete(3L));
        UserRequest promote = new UserRequest();
        promote.setName("Garcom");
        promote.setEmail("u4@example.com");
        promote.setRole("ADMIN");
        assertThrows(AccessDeniedException.class, () -> service.update(4L, promote));
        service.delete(4L);
        verify(users).save(argThat(u -> u.getId() == 4L && u.getDeletedAt() != null));
    }

    @Test
    void responseFlagsRoot() {
        loggedAs(1);
        assertTrue(service.getById(1L).isRoot());
        assertFalse(service.getById(user(4, UserRole.WAITER).getId()).isRoot());
    }
}
