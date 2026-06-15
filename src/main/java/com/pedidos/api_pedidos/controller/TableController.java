package com.pedidos.api_pedidos.controller;

import com.pedidos.api_pedidos.domain.entity.TableEntity;
import com.pedidos.api_pedidos.dto.auth.TableAuthRequest;
import com.pedidos.api_pedidos.dto.auth.TableAuthResponse;
import com.pedidos.api_pedidos.dto.table.GenerateCodesResponse;
import com.pedidos.api_pedidos.dto.table.TableRequest;
import com.pedidos.api_pedidos.dto.table.TableResponse;
import com.pedidos.api_pedidos.exception.UnauthorizedException;
import com.pedidos.api_pedidos.repository.TableRepository;
import com.pedidos.api_pedidos.security.JwtUtil;
import com.pedidos.api_pedidos.security.TokenBlacklist;
import com.pedidos.api_pedidos.service.TableService;
import com.pedidos.api_pedidos.shared.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tables")
public class TableController {

    private final TableService service;
    private final TableRepository tableRepository;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    public TableController(TableService service,
                           TableRepository tableRepository,
                           JwtUtil jwtUtil,
                           TokenBlacklist tokenBlacklist) {
        this.service = service;
        this.tableRepository = tableRepository;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;
    }


    @PostMapping
    public ResponseEntity<ApiResponse<TableResponse>> create(@RequestBody TableRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TableResponse>> update(
            @PathVariable Long id,
            @RequestBody TableRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.update(id, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TableResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TableResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<TableResponse>> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByCode(code)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Mesa removida", null));
    }

    @PostMapping("/{id}/generate-codes")
    public ResponseEntity<ApiResponse<GenerateCodesResponse>> generateCodes(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Códigos gerados", service.generateCodes(id)));
    }


    @PostMapping("/auth")
    public ResponseEntity<ApiResponse<TableAuthResponse>> tableAuth(@Valid @RequestBody TableAuthRequest request) {
        TableEntity table = tableRepository.findByCode(request.getCode())
                .orElseThrow(() -> new UnauthorizedException("Código de mesa inválido"));

        String token = jwtUtil.generateTableToken(table);
        TableAuthResponse response = new TableAuthResponse(token, table.getId(), table.getCode());

        return ResponseEntity.ok(ApiResponse.ok("Autenticação realizada", response));
    }


    @PutMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> tableLogout(Authentication authentication) {
        if (authentication != null && authentication.getCredentials() != null) {
            String token = authentication.getCredentials().toString();
            tokenBlacklist.add(token);
        }
        return ResponseEntity.ok(ApiResponse.ok("Logout da mesa realizado", null));
    }
}