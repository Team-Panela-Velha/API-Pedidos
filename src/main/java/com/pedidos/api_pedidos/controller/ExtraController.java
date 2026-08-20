package com.pedidos.api_pedidos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pedidos.api_pedidos.dto.extra.ExtraRequest;
import com.pedidos.api_pedidos.dto.extra.ExtraResponse;
import com.pedidos.api_pedidos.service.ExtraService;

import java.util.List;

@RestController
@RequestMapping("/extras")
public class ExtraController {

    private final ExtraService service;

    public ExtraController(ExtraService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ExtraResponse> create(@RequestBody ExtraRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExtraResponse> update(
            @PathVariable Long id,
            @RequestBody ExtraRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping
    public ResponseEntity<List<ExtraResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtraResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
