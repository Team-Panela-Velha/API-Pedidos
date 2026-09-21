package com.pedidos.api_pedidos.dto.product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.pedidos.api_pedidos.dto.extra.ExtraResponse;

public class ProductResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String image;
    private Long categoryId;
    private Boolean available;
    private List<ExtraResponse> extras;
    private Instant createdAt;
    private Long createdBy;
    private Instant updatedAt;
    private Long updatedBy;
    private Instant deletedAt;
    private Long deletedBy;

    public ProductResponse(Long id, String name, BigDecimal price, String description, String image, Long categoryId, Boolean available) {
        this(id, name, price, description, image, categoryId, available, null);
    }

    public ProductResponse(Long id, String name, BigDecimal price, String description, String image, Long categoryId, Boolean available, List<ExtraResponse> extras) {
        this(id, name, price, description, image, categoryId, available, extras, null, null, null, null, null, null);
    }

    public ProductResponse(Long id, String name, BigDecimal price, String description, String image, Long categoryId,
                           Boolean available, List<ExtraResponse> extras, Instant createdAt, Long createdBy,
                           Instant updatedAt, Long updatedBy, Instant deletedAt, Long deletedBy) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.categoryId = categoryId;
        this.available = available;
        this.extras = extras;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getDescription() { return description; }
    public String getImage() { return image; }
    public Long getCategoryId() { return categoryId; }
    public Boolean getAvailable() { return available; }
    public List<ExtraResponse> getExtras() { return extras; }
    public Instant getCreatedAt() { return createdAt; }
    public Long getCreatedBy() { return createdBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getUpdatedBy() { return updatedBy; }
    public Instant getDeletedAt() { return deletedAt; }
    public Long getDeletedBy() { return deletedBy; }
}
