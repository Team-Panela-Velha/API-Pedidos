package com.pedidos.api_pedidos.controller;

import org.springframework.web.bind.annotation.*;

import com.pedidos.api_pedidos.dto.table.TableRequest;
import com.pedidos.api_pedidos.dto.table.TableResponse;
import com.pedidos.api_pedidos.service.TableService;

import java.util.List;

@RestController
@RequestMapping("/tables")
public class TableController {
    private final TableService service;

    public TableController(TableService service) {
        this.service = service;
    }

    @PostMapping
    public TableResponse create(@RequestBody TableRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public TableResponse update(
            @PathVariable Long id,
            @RequestBody TableRequest request
    ) {
        return service.update(id, request);
    }

    @GetMapping
    public List<TableResponse> getAll() {     
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TableResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/code/{code}")
    public TableResponse getByCode(@PathVariable String code) {
        return service.getByCode(code);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}