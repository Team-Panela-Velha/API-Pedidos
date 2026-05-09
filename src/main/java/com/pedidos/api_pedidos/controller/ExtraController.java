package com.pedidos.api_pedidos.controller;

import com.pedidos.api_pedidos.dto.extra.ExtraRequest;
import com.pedidos.api_pedidos.dto.extra.ExtraResponse;
import com.pedidos.api_pedidos.service.ExtraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/extras")
public class ExtraController {

    private final ExtraService extraService;

    public ExtraController(ExtraService extraService) {
        this.extraService = extraService;
    }

    @PostMapping
    public ResponseEntity<?> createExtra(@RequestBody ExtraRequest request) {
        try {
            ExtraResponse response = extraService.createExtra(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ExtraResponse>> getAllExtras() {
        return ResponseEntity.ok(extraService.getAllExtras());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtraResponse> getExtraById(@PathVariable UUID id) {
        ExtraResponse response = extraService.getExtraById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExtra(@PathVariable UUID id, @RequestBody ExtraRequest request) {
        try {
            ExtraResponse response = extraService.updateExtra(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExtra(@PathVariable UUID id) {
        extraService.deleteExtra(id);
        return ResponseEntity.noContent().build();
    }
}
