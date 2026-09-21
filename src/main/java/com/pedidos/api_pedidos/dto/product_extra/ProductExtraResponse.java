package com.pedidos.api_pedidos.dto.product_extra;

import java.time.Instant;

public class ProductExtraResponse {

    private Long id;
    private Long productId;
    private Long extraId;
    private Instant createdAt;

    public ProductExtraResponse(Long id, Long productId, Long extraId, Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.extraId = extraId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public Long getExtraId() { return extraId; }
    public Instant getCreatedAt() { return createdAt; }
}
