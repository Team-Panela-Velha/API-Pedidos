package com.pedidos.api_pedidos.dto.product;

import java.math.BigDecimal;
import java.util.List;

import com.pedidos.api_pedidos.dto.extra.ExtraResponse;

public class ProductResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String image;
    private Long categoryId;
    private List<ExtraResponse> extras;

    public ProductResponse(Long id, String name, BigDecimal price, String description, String image, Long categoryId) {
        this(id, name, price, description, image, categoryId, null);
    }

    public ProductResponse(Long id, String name, BigDecimal price, String description, String image, Long categoryId, List<ExtraResponse> extras) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.categoryId = categoryId;
        this.extras = extras;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImage() { return image; }
    public Long getCategoryId() { return categoryId; }
    public List<ExtraResponse> getExtras() { return extras; }
}
