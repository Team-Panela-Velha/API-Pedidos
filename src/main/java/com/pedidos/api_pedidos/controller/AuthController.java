package com.pedidos.api_pedidos.controller;

import com.pedidos.api_pedidos.dto.auth.AuthResponse;
import com.pedidos.api_pedidos.dto.auth.LoginRequest;
import com.pedidos.api_pedidos.dto.auth.RegisterRequest;
import com.pedidos.api_pedidos.dto.user.UserResponse;
import com.pedidos.api_pedidos.security.TokenBlacklist;
import com.pedidos.api_pedidos.security.JwtUtil;
import com.pedidos.api_pedidos.service.UserService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final TokenBlacklist tokenBlacklist;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, TokenBlacklist tokenBlacklist, JwtUtil jwtUtil) {
        this.userService = userService;
        this.tokenBlacklist = tokenBlacklist;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @SecurityRequirements
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    @SecurityRequirements
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = userService.login(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        if (authentication != null && authentication.getCredentials() != null) {
            String token = authentication.getCredentials().toString();
            if (jwtUtil.validateToken(token)) {
                tokenBlacklist.add(token);
            }
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return userService.getByEmail(authentication.getName());
    }
}
