package com.pedidos.api_pedidos.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pedidos.api_pedidos.domain.entity.CategoryEntity;
import com.pedidos.api_pedidos.domain.entity.ProductDietaryRestrictionEntity;
import com.pedidos.api_pedidos.domain.entity.ProductEntity;
import com.pedidos.api_pedidos.domain.enums.DietaryRestriction;
import com.pedidos.api_pedidos.dto.product.ProductRequest;
import com.pedidos.api_pedidos.dto.product.ProductResponse;
import com.pedidos.api_pedidos.repository.CategoryRepository;
import com.pedidos.api_pedidos.repository.OrderItemRepository;
import com.pedidos.api_pedidos.repository.ProductDietaryRestrictionRepository;
import com.pedidos.api_pedidos.repository.ProductExtraRepository;
import com.pedidos.api_pedidos.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    private final ProductDietaryRestrictionRepository productDietaryRestrictionRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductExtraRepository productExtraRepository;

    public ProductService(ProductRepository repository,
                          CategoryRepository categoryRepository,
                          ProductDietaryRestrictionRepository productDietaryRestrictionRepository,
                          OrderItemRepository orderItemRepository,
                          ProductExtraRepository productExtraRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.productDietaryRestrictionRepository = productDietaryRestrictionRepository;
        this.orderItemRepository = orderItemRepository;
        this.productExtraRepository = productExtraRepository;
    }

    public ProductResponse create(ProductRequest request) {
        validatePrice(request.getPrice());

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Category not found"));

        ProductEntity entity = new ProductEntity(
                request.getName(), request.getPrice(), request.getDescription(), request.getImage(), category);
        entity.setAvailable(request.getAvailable() != null ? request.getAvailable() : true);
        entity = repository.save(entity);

        return toResponseWithExtras(entity);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        validatePrice(request.getPrice());

        ProductEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Category not found"));

        entity.setName(request.getName());
        entity.setPrice(request.getPrice());
        entity.setDescription(request.getDescription());
        entity.setImage(request.getImage());
        entity.setCategory(category);
        entity.setAvailable(request.getAvailable() != null ? request.getAvailable() : entity.getAvailable());
        entity = repository.save(entity);

        return toResponseWithExtras(entity);
    }

    public List<ProductResponse> getAll(Boolean available, List<DietaryRestriction> restrictions) {
        List<ProductEntity> products = repository.findAll();

        // Exclude products that have any of the requested restrictions
        if (restrictions != null && !restrictions.isEmpty()) {
            List<ProductDietaryRestrictionEntity> entries = productDietaryRestrictionRepository.findByRestrictionIn(restrictions);
            Set<Long> excludedProductIds = entries.stream()
                    .map(e -> e.getProduct().getId())
                    .collect(Collectors.toSet());
            products = products.stream()
                    .filter(p -> !excludedProductIds.contains(p.getId()))
                    .collect(Collectors.toList());
        }

        if (available != null) {
            products = products.stream()
                    .filter(p -> available.equals(p.getAvailable()))
                    .collect(Collectors.toList());
        }

        return products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getById(Long id) {
        ProductEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return toResponseWithExtras(entity);
    }

    /**
     * Função nova 1 — Lista os produtos de uma categoria.
     * 404 se a categoria não existir. Aceita o filtro opcional ?available=true.
     */
    public List<ProductResponse> getByCategory(Long categoryId, Boolean available) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }

        List<ProductEntity> products = repository.findByCategoryId(categoryId);

        if (available != null) {
            products = products.stream()
                    .filter(p -> available.equals(p.getAvailable()))
                    .collect(Collectors.toList());
        }

        return products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Função nova 2 — Busca produtos por nome + descrição + nome da categoria.
     * Termo vazio/ausente retorna lista vazia (evita payload grande sem intenção de busca).
     */
    public List<ProductResponse> search(String q) {
        if (q == null || q.isBlank()) {
            return List.of();
        }
        return repository.search(q.trim())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        // Check if product is present in any order_item of an open tab
        List<com.pedidos.api_pedidos.domain.entity.OrderItemEntity> items = orderItemRepository.findByProductId(id);
        boolean inOpenOrder = items.stream().anyMatch(item ->
                item.getOrder() != null && Boolean.FALSE.equals(item.getOrder().getTab().getClosed())
        );
        if (inOpenOrder) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete product that exists in open orders");
        }

        repository.deleteById(id);
    }

    private void validatePrice(java.math.BigDecimal price) {
        if (price == null || price.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Price must be greater than 0");
        }
    }

    private ProductResponse toResponse(ProductEntity entity) {
        Long categoryId = entity.getCategory() != null ? entity.getCategory().getId() : null;
        return new ProductResponse(
                entity.getId(), entity.getName(), entity.getPrice(),
                entity.getDescription(), entity.getImage(), categoryId);
    }

    private ProductResponse toResponseWithExtras(ProductEntity entity) {
        Long categoryId = entity.getCategory() != null ? entity.getCategory().getId() : null;
        java.util.List<com.pedidos.api_pedidos.dto.extra.ExtraResponse> extras = productExtraRepository.findByProductId(entity.getId())
            .stream()
            .map(pe -> pe.getExtra())
            .map(e -> new com.pedidos.api_pedidos.dto.extra.ExtraResponse(e.getId(), e.getName(), e.getPrice()))
            .collect(Collectors.toList());
        return new ProductResponse(
                entity.getId(), entity.getName(), entity.getPrice(),
                entity.getDescription(), entity.getImage(), categoryId, extras);
    }
}