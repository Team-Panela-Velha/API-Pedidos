package com.pedidos.api_pedidos.service;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.ExtraEntity;
import com.pedidos.api_pedidos.domain.entity.ProductEntity;
import com.pedidos.api_pedidos.domain.entity.ProductExtraEntity;
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

    public ProductExtraResponse create(ProductExtraRequest request) {
        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        ExtraEntity extra = extraRepository.findById(request.getExtraId())
                .orElseThrow(() -> new RuntimeException("Extra not found"));

        ProductExtraEntity entity = new ProductExtraEntity(product, extra);
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public List<ProductExtraResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductExtraResponse> getByProductId(Long productId) {
        return repository.findByProductId(productId)
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
        return new ProductExtraResponse(entity.getId(), productId, extraId);
    }
}
