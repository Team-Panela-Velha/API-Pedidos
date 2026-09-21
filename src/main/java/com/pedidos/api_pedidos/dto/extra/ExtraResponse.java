package com.pedidos.api_pedidos.dto.extra;

import java.math.BigDecimal;
import java.time.Instant;

public class ExtraResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private Instant createdAt;
    private Long createdBy;
    private Instant updatedAt;
    private Long updatedBy;
    private Instant deletedAt;
    private Long deletedBy;

    public ExtraResponse(Long id, String name, BigDecimal price, Instant createdAt, Long createdBy,
                         Instant updatedAt, Long updatedBy, Instant deletedAt, Long deletedBy) {
        this.id = id;
        this.name = name;
        this.price = price;
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
    public Instant getCreatedAt() { return createdAt; }
    public Long getCreatedBy() { return createdBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getUpdatedBy() { return updatedBy; }
    public Instant getDeletedAt() { return deletedAt; }
    public Long getDeletedBy() { return deletedBy; }
}
