package com.pedidos.api_pedidos.service;

import com.pedidos.api_pedidos.domain.entity.UserEntity;
import com.pedidos.api_pedidos.domain.enums.UserRole;
import com.pedidos.api_pedidos.dto.auth.AuthResponse;
import com.pedidos.api_pedidos.dto.auth.LoginRequest;
import com.pedidos.api_pedidos.dto.auth.RegisterRequest;
import com.pedidos.api_pedidos.dto.user.UserRequest;
import com.pedidos.api_pedidos.dto.user.UserResponse;
import com.pedidos.api_pedidos.exception.ConflictException;
import com.pedidos.api_pedidos.exception.ResourceNotFoundException;
import com.pedidos.api_pedidos.exception.UnauthorizedException;
import com.pedidos.api_pedidos.repository.UserRepository;
import com.pedidos.api_pedidos.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuditService auditService;

    public UserService(UserRepository repository,
                            PasswordEncoder passwordEncoder,
                            JwtUtil jwtUtil,
                            AuditService auditService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.auditService = auditService;
    }


    public synchronized UserResponse register(RegisterRequest request) {
        boolean firstUser = repository.countByDeletedAtIsNull() == 0;
        if (!firstUser && SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new AccessDeniedException("Somente administradores podem cadastrar usuários");
        }
        if (!firstUser && SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Somente administradores podem cadastrar usuários");
        }
        if (repository.existsByEmail(request.getEmail())) {
            throw new ConflictException("E-mail já cadastrado: " + request.getEmail());
        }

        UserRole role = firstUser ? UserRole.ADMIN : request.getRole() == null
                ? UserRole.WAITER : UserRole.valueOf(request.getRole().toUpperCase());
        if (!firstUser && isAdminLevel(role) && !callerIsRoot()) {
            throw new AccessDeniedException("Somente o administrador global pode criar administradores");
        }

        UserEntity entity = new UserEntity(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                role
        );
        entity = repository.save(entity);
        return toResponse(entity);
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity entity = repository.findByEmailAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        if (Boolean.FALSE.equals(entity.getActive())
                || !passwordEncoder.matches(request.getPassword(), entity.getPasswordHash())) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        String token = jwtUtil.generateUserToken(entity);
        return new AuthResponse(
                token,
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getRole().name()
        );
    }


    public List<UserResponse> getAll() {
        return repository.findAllByDeletedAtIsNull()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public UserResponse getByEmail(String email) {
        return toResponse(repository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado")));
    }

    public UserResponse update(Long id, UserRequest request) {
        UserEntity entity = findOrThrow(id);
        UserRole newRole = null;
        if (request.getRole() != null) {
            try {
                newRole = UserRole.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (isRoot(entity)) {
            if ((newRole != null && newRole != UserRole.ADMIN) || Boolean.FALSE.equals(request.getActive())) {
                throw new AccessDeniedException("O administrador global não pode ser rebaixado nem desativado");
            }
            if (!callerIsRoot()) {
                throw new AccessDeniedException("Somente o administrador global pode alterar a própria conta");
            }
        } else if ((isAdminLevel(entity.getRole()) || isAdminLevel(newRole)) && !callerIsRoot()) {
            throw new AccessDeniedException("Somente o administrador global pode gerenciar administradores");
        }

        if (!request.getEmail().equalsIgnoreCase(entity.getEmail()) && repository.existsByEmail(request.getEmail())) {
            throw new ConflictException("E-mail já cadastrado: " + request.getEmail());
        }
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        if (request.getActive() != null) entity.setActive(request.getActive());
        if (newRole != null) entity.setRole(newRole);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            entity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        return toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        UserEntity entity = findOrThrow(id);
        if (isRoot(entity)) {
            throw new AccessDeniedException("O administrador global não pode ser excluído");
        }
        if (isAdminLevel(entity.getRole()) && !callerIsRoot()) {
            throw new AccessDeniedException("Somente o administrador global pode excluir administradores");
        }
        entity.markDeleted(auditService.currentUserId());
        repository.save(entity);
    }


    private UserEntity findOrThrow(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    // Global admin ("root") = oldest active ADMIN. It cannot be deleted, demoted or deactivated,
    // so it never changes and needs no extra column.
    private Long rootId() {
        return repository.findFirstByRoleAndDeletedAtIsNullOrderByIdAsc(UserRole.ADMIN)
                .map(UserEntity::getId).orElse(null);
    }

    private boolean isRoot(UserEntity entity) {
        return entity.getId() != null && entity.getId().equals(rootId());
    }

    private boolean callerIsRoot() {
        return auditService.currentUserId().equals(rootId());
    }

    private static boolean isAdminLevel(UserRole role) {
        return role == UserRole.ADMIN || role == UserRole.MANAGER;
    }

    private UserResponse toResponse(UserEntity entity) {
        UserResponse response = new UserResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getRole().name(),
                entity.getCreatedAt(), entity.getCreatedBy(), entity.getUpdatedAt(), entity.getUpdatedBy(),
                entity.getDeletedAt(), entity.getDeletedBy()
        );
        response.setRoot(isRoot(entity));
        return response;
    }
}
