package com.pedidos.api_pedidos.service;

import com.pedidos.api_pedidos.domain.entity.StaffUserEntity;
import com.pedidos.api_pedidos.domain.enums.StaffRole;
import com.pedidos.api_pedidos.dto.auth.AuthResponse;
import com.pedidos.api_pedidos.dto.auth.LoginRequest;
import com.pedidos.api_pedidos.dto.auth.RegisterRequest;
import com.pedidos.api_pedidos.dto.staff_user.StaffUserRequest;
import com.pedidos.api_pedidos.dto.staff_user.StaffUserResponse;
import com.pedidos.api_pedidos.exception.ConflictException;
import com.pedidos.api_pedidos.exception.ResourceNotFoundException;
import com.pedidos.api_pedidos.exception.UnauthorizedException;
import com.pedidos.api_pedidos.repository.StaffUserRepository;
import com.pedidos.api_pedidos.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffUserService {

    private final StaffUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public StaffUserService(StaffUserRepository repository,
                            PasswordEncoder passwordEncoder,
                            JwtUtil jwtUtil) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    public StaffUserResponse register(RegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new ConflictException("E-mail já cadastrado: " + request.getEmail());
        }

        StaffRole role = StaffRole.WAITER;
        if (request.getRole() != null) {
            try {
                role = StaffRole.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        StaffUserEntity entity = new StaffUserEntity(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                role
        );
        entity = repository.save(entity);
        return toResponse(entity);
    }

    public AuthResponse login(LoginRequest request) {
        StaffUserEntity entity = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), entity.getPasswordHash())) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        String token = jwtUtil.generateStaffToken(entity);
        return new AuthResponse(
                token,
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getRole().name()
        );
    }


    public List<StaffUserResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public StaffUserResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public StaffUserResponse update(Long id, StaffUserRequest request) {
        StaffUserEntity entity = findOrThrow(id);
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());

        if (request.getRole() != null) {
            try {
                entity.setRole(StaffRole.valueOf(request.getRole().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        findOrThrow(id);
        repository.deleteById(id);
    }


    private StaffUserEntity findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    private StaffUserResponse toResponse(StaffUserEntity entity) {
        return new StaffUserResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getRole().name()
        );
    }
}
