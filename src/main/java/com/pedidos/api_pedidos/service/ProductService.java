package com.pedidos.api_pedidos.service;

import java.util.stream.Collectors;

import com.pedidos.api_pedidos.domain.entity.ProductEntity;
import com.pedidos.api_pedidos.dto.product.ProductRequest;
import com.pedidos.api_pedidos.dto.product.ProductResponse;
import com.pedidos.api_pedidos.repository.ProductRepository;

public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public ProductResponse create(ProductRequest request) {
        ProductEntity entity = new ProductEntity(request.getCode());
        entity = repository.save(entity);

        return new ProductResponse(entity.getId(), entity.getCode());
    }

    public ProductResponse update(Long id, ProductRequest request) {
        ProductEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        entity.setCode(request.getCode());

        entity = repository.save(entity);

        return new ProductResponse(entity.getId(), entity.getCode());
    }

    public List<ProductResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(e -> new ProductResponse(e.getId(), e.getCode()))
                .collect(Collectors.toList());
    }

    public ProductResponse getById(Long id) {
        ProductEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return new ProductResponse(entity.getId(), entity.getCode());
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
