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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository repository,
                            PasswordEncoder passwordEncoder,
                            JwtUtil jwtUtil) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    public UserResponse register(RegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new ConflictException("E-mail já cadastrado: " + request.getEmail());
        }

        UserRole role = UserRole.WAITER;
        if (request.getRole() != null) {
            try {
                role = UserRole.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
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
        UserEntity entity = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), entity.getPasswordHash())) {
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
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public UserResponse update(Long id, UserRequest request) {
        UserEntity entity = findOrThrow(id);
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());

        if (request.getRole() != null) {
            try {
                entity.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        findOrThrow(id);
        repository.deleteById(id);
    }


    private UserEntity findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    private UserResponse toResponse(UserEntity entity) {
        return new UserResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getRole().name()
        );
    }
}
