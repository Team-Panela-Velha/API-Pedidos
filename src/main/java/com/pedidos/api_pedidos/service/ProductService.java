package com.pedidos.api_pedidos.service;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.CategoryEntity;
import com.pedidos.api_pedidos.domain.entity.ProductEntity;
import com.pedidos.api_pedidos.dto.product.ProductRequest;
import com.pedidos.api_pedidos.dto.product.ProductResponse;
import com.pedidos.api_pedidos.repository.CategoryRepository;
import com.pedidos.api_pedidos.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse create(ProductRequest request) {
        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ProductEntity entity = new ProductEntity(
                request.getName(), request.getPrice(), request.getDescription(), request.getImage(), category);
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        ProductEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        entity.setName(request.getName());
        entity.setPrice(request.getPrice());
        entity.setDescription(request.getDescription());
        entity.setCategory(category);
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public List<ProductResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getById(Long id) {
        ProductEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toResponse(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private ProductResponse toResponse(ProductEntity entity) {
        Long categoryId = entity.getCategory() != null ? entity.getCategory().getId() : null;
        return new ProductResponse(
                entity.getId(), entity.getName(), entity.getPrice(), entity.getDescription(), entity.getImage(), categoryId);
    }
}
