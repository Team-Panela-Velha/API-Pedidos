package com.pedidos.api_pedidos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.CategoryEntity;
import com.pedidos.api_pedidos.dto.category.CategoryRequest;
import com.pedidos.api_pedidos.dto.category.CategoryResponse;
import com.pedidos.api_pedidos.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public CategoryResponse create(CategoryRequest request) {
        CategoryEntity entity = new CategoryEntity(request.getCode());
        entity = repository.save(entity);

        return new CategoryResponse(entity.getId(), entity.getCode());
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        CategoryEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        entity.setCode(request.getCode());

        entity = repository.save(entity);

        return new CategoryResponse(entity.getId(), entity.getCode());
    }

    public List<CategoryResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(e -> new CategoryResponse(e.getId(), e.getCode()))
                .collect(Collectors.toList());
    }

    public CategoryResponse getById(Long id) {
        CategoryEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return new CategoryResponse(entity.getId(), entity.getCode());
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
