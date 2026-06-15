package com.pedidos.api_pedidos.controller;

import com.pedidos.api_pedidos.dto.auth.AuthResponse;
import com.pedidos.api_pedidos.dto.auth.LoginRequest;
import com.pedidos.api_pedidos.dto.auth.RegisterRequest;
import com.pedidos.api_pedidos.dto.staff_user.StaffUserResponse;
import com.pedidos.api_pedidos.security.TokenBlacklist;
import com.pedidos.api_pedidos.service.StaffUserService;
import com.pedidos.api_pedidos.shared.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final StaffUserService staffUserService;
    private final TokenBlacklist tokenBlacklist;

    public AuthController(StaffUserService staffUserService, TokenBlacklist tokenBlacklist) {
        this.staffUserService = staffUserService;
        this.tokenBlacklist = tokenBlacklist;
    }

    /**
     * POST /auth/register
     * Recebe: name, email, password
     * Retorna: 201 com dados do usuário
     * Lança: 409 se e-mail já existir
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<StaffUserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        StaffUserResponse response = staffUserService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    /**
     * POST /auth/login
     * Recebe: email, password
     * Retorna: 200 com JWT (role e id nos claims)
     * Lança: 401 se inválido
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = staffUserService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login realizado com sucesso", authResponse));
    }

    /**
     * POST /auth/logout
     * Requer: Bearer token válido
     * Adiciona o token à blacklist
     * Retorna: 200
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        if (authentication != null && authentication.getCredentials() != null) {
            String token = authentication.getCredentials().toString();
            tokenBlacklist.add(token);
        }
        return ResponseEntity.ok(ApiResponse.ok("Logout realizado com sucesso", null));
    }
}
