package com.pedidos.api_pedidos.controller;

import org.springframework.web.bind.annotation.*;

import com.pedidos.api_pedidos.dto.tab.TabRequest;
import com.pedidos.api_pedidos.dto.tab.TabResponse;
import com.pedidos.api_pedidos.service.TabService;

import java.util.List;

@RestController
@RequestMapping("/tabs")
public class TabController {

    private final TabService service;

    public TabController(TabService service) {
        this.service = service;
    }

    @PostMapping
    public TabResponse create(@RequestBody TabRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public TabResponse update(@PathVariable Long id, @RequestBody TabRequest request) {
        return service.update(id, request);
    }

    @GetMapping
    public List<TabResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TabResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
