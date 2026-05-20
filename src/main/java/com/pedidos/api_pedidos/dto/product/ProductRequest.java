package com.pedidos.api_pedidos.dto.product;

import java.math.BigDecimal;

public class ProductRequest {

    private String name;
    private BigDecimal price;
    private String description;
    private Long categoryId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}
