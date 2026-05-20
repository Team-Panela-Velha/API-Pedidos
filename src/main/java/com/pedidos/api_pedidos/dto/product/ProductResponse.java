package com.pedidos.api_pedidos.dto.product;

import java.math.BigDecimal;

public class ProductResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private Long categoryId;

    public ProductResponse(Long id, String name, BigDecimal price, String description, Long categoryId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getDescription() { return description; }
    public Long getCategoryId() { return categoryId; }
}
