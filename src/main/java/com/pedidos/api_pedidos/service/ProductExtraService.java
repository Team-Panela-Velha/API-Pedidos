package com.pedidos.api_pedidos.service;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.ExtraEntity;
import com.pedidos.api_pedidos.domain.entity.ProductEntity;
import com.pedidos.api_pedidos.domain.entity.ProductExtraEntity;
import com.pedidos.api_pedidos.dto.extra.ExtraResponse;
import com.pedidos.api_pedidos.dto.product_extra.ProductExtraRequest;
import com.pedidos.api_pedidos.dto.product_extra.ProductExtraResponse;
import com.pedidos.api_pedidos.repository.ExtraRepository;
import com.pedidos.api_pedidos.repository.ProductExtraRepository;
import com.pedidos.api_pedidos.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductExtraService {

    private final ProductExtraRepository repository;
    private final ProductRepository productRepository;
    private final ExtraRepository extraRepository;

    public ProductExtraService(ProductExtraRepository repository,
                               ProductRepository productRepository,
                               ExtraRepository extraRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.extraRepository = extraRepository;
    }

    /**
     * Retorna todos os extras disponíveis para um produto específico.
     */
    public List<ExtraResponse> getProductExtras(Long productId) {
        productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return repository.findActiveByProductId(productId)
                .stream()
                .map(pe -> {
                    ExtraEntity extra = pe.getExtra();
                    return toExtraResponse(extra);
                })
                .collect(Collectors.toList());
    }

    // ── CRUD padrão ───────────────────────────────────────────────────────────

    public ProductExtraResponse create(ProductExtraRequest request) {
        ProductEntity product = productRepository.findByIdAndDeletedAtIsNull(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        ExtraEntity extra = extraRepository.findByIdAndDeletedAtIsNull(request.getExtraId())
                .orElseThrow(() -> new RuntimeException("Extra not found"));

        ProductExtraEntity entity = new ProductExtraEntity(product, extra);
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public List<ProductExtraResponse> getAll() {
        return repository.findAllActive()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductExtraResponse> getByProductId(Long productId) {
        return repository.findActiveByProductId(productId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductExtraResponse getById(Long id) {
        ProductExtraEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ProductExtra not found"));
        return toResponse(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private ProductExtraResponse toResponse(ProductExtraEntity entity) {
        Long productId = entity.getProduct() != null ? entity.getProduct().getId() : null;
        Long extraId = entity.getExtra() != null ? entity.getExtra().getId() : null;
        return new ProductExtraResponse(entity.getId(), productId, extraId, entity.getCreatedAt());
    }

    private ExtraResponse toExtraResponse(ExtraEntity extra) {
        return new ExtraResponse(extra.getId(), extra.getName(), extra.getPrice(),
                extra.getCreatedAt(), extra.getCreatedBy(), extra.getUpdatedAt(), extra.getUpdatedBy(),
                extra.getDeletedAt(), extra.getDeletedBy());
    }
}
