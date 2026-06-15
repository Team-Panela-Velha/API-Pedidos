package com.pedidos.api_pedidos.service;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.CategoryEntity;
import com.pedidos.api_pedidos.dto.category.CategoryRequest;
import com.pedidos.api_pedidos.dto.category.CategoryResponse;
import com.pedidos.api_pedidos.exception.ConflictException;
import com.pedidos.api_pedidos.exception.ResourceNotFoundException;
import com.pedidos.api_pedidos.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public CategoryResponse create(CategoryRequest request) {
        CategoryEntity entity = new CategoryEntity(request.getName(), request.getDescription());
        entity = repository.save(entity);
        return toResponse(entity);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        CategoryEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public List<CategoryResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getById(Long id) {
        CategoryEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
        return toResponse(entity);
    }

    /**
     * DELETE — retorna 409 se houver produtos associados à categoria
     */
    public void delete(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));

        if (repository.existsProductByCategoryId(id)) {
            throw new ConflictException("Não é possível remover: categoria possui produtos associados");
        }

        repository.deleteById(id);
    }

    private CategoryResponse toResponse(CategoryEntity entity) {
        return new CategoryResponse(entity.getId(), entity.getName(), entity.getDescription());
    }
}
