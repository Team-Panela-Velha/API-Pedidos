package com.pedidos.api_pedidos.dto.product_extra;

public class ProductExtraRequest {

    private Long productId;
    private Long extraId;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getExtraId() { return extraId; }
    public void setExtraId(Long extraId) { this.extraId = extraId; }
}
