package com.pedidos.api_pedidos.controller;

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
    public ExtraResponse create(@RequestBody ExtraRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public ExtraResponse update(@PathVariable Long id, @RequestBody ExtraRequest request) {
        return service.update(id, request);
    }

    @GetMapping
    public List<ExtraResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ExtraResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
