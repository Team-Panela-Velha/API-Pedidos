package com.pedidos.api_pedidos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pedidos.api_pedidos.dto.extra.ExtraRequest;
import com.pedidos.api_pedidos.dto.extra.ExtraResponse;
import com.pedidos.api_pedidos.service.ExtraService;
import com.pedidos.api_pedidos.shared.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/extras")
public class ExtraController {

    private final ExtraService service;

    public ExtraController(ExtraService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExtraResponse>> create(@RequestBody ExtraRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(service.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExtraResponse>> update(
            @PathVariable Long id,
            @RequestBody ExtraRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Extra atualizado", service.update(id, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExtraResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(service.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExtraResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Extra removido", null));
    }
}
