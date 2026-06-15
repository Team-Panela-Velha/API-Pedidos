package com.pedidos.api_pedidos.controller;

import com.pedidos.api_pedidos.dto.staff_user.StaffUserRequest;
import com.pedidos.api_pedidos.dto.staff_user.StaffUserResponse;
import com.pedidos.api_pedidos.service.StaffUserService;
import com.pedidos.api_pedidos.shared.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff-users")
public class StaffUserController {

    private final StaffUserService service;

    public StaffUserController(StaffUserService service) {
        this.service = service;
    }

    /**
     * GET /staff-users
     * Lista todos os usuários staff
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<StaffUserResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    /**
     * GET /staff-users/{id}
     * Retorna 404 se não encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffUserResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    /**
     * PUT /staff-users/{id}
     * Atualiza name, email, role — NUNCA atualiza senha
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffUserResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody StaffUserRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Usuário atualizado", service.update(id, request)));
    }

    /**
     * DELETE /staff-users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuário removido", null));
    }
}
