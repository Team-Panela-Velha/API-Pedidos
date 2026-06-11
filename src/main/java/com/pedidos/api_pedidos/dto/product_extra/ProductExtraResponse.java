package com.pedidos.api_pedidos.dto.product_extra;

public class ProductExtraResponse {

    private Long id;
    private Long productId;
    private Long extraId;

    public ProductExtraResponse(Long id, Long productId, Long extraId) {
        this.id = id;
        this.productId = productId;
        this.extraId = extraId;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public Long getExtraId() { return extraId; }
}
