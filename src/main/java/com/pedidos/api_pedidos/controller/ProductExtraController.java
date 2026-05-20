package com.pedidos.api_pedidos.controller;

import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/by-product/{productId}")
    public List<ProductExtraResponse> getByProductId(@PathVariable Long productId) {
        return service.getByProductId(productId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
