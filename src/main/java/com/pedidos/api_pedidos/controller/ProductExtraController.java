package com.pedidos.api_pedidos.controller;

import org.springframework.web.bind.annotation.*;

import com.pedidos.api_pedidos.dto.extra.ExtraResponse;
import com.pedidos.api_pedidos.dto.product_extra.ProductExtraRequest;
import com.pedidos.api_pedidos.dto.product_extra.ProductExtraResponse;
import com.pedidos.api_pedidos.service.ProductExtraService;

import java.util.List;

@RestController
@RequestMapping("/product-extras")
public class ProductExtraController {

    private final ProductExtraService service;

    public ProductExtraController(ProductExtraService service) {
        this.service = service;
    }

    // ── Endpoints solicitados ─────────────────────────────────────────────────

    @GetMapping("/by-product/{productId}/extras")
    public List<ExtraResponse> getProductExtras(@PathVariable Long productId) {
        return service.getProductExtras(productId);
    }

    // ── CRUD padrão ───────────────────────────────────────────────────────────

    @PostMapping
    public ProductExtraResponse create(@RequestBody ProductExtraRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ProductExtraResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ProductExtraResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
