package com.pedidos.api_pedidos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.pedidos.api_pedidos.dto.product.ProductRequest;
import com.pedidos.api_pedidos.dto.product.ProductResponse;
import com.pedidos.api_pedidos.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ProductResponse create(@RequestBody ProductRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @GetMapping
    public List<ProductResponse> getAll(@RequestParam(value = "available", required = false) Boolean available,
                                        @RequestParam(value = "restrictions", required = false) List<com.pedidos.api_pedidos.domain.enums.DietaryRestriction> restrictions) {
        return service.getAll(available, restrictions);
    }

    // Função nova 1 — produtos por categoria
    @GetMapping("/category/{categoryId}")
    public List<ProductResponse> getByCategory(@PathVariable Long categoryId,
                                               @RequestParam(value = "available", required = false) Boolean available) {
        return service.getByCategory(categoryId, available);
    }

    // Função nova 2 — busca (search bar)
    @GetMapping("/search")
    public List<ProductResponse> search(@RequestParam(value = "q", required = false) String q) {
        return service.search(q);
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
