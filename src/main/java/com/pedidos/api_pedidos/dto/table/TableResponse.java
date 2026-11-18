package com.pedidos.api_pedidos.dto.table;

import java.time.Instant;

public class TableResponse {

    private Long id;
    private String code;
    private Integer number;
    private Instant createdAt;
    private Long createdBy;
    private Instant updatedAt;
    private Long updatedBy;
    private Instant deletedAt;
    private Long deletedBy;

    public TableResponse(Long id, String code, Integer number, Instant createdAt, Long createdBy,
                         Instant updatedAt, Long updatedBy, Instant deletedAt, Long deletedBy) {
        this.id = id;
        this.code = code;
        this.number = number;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Integer getNumber() { return number; }

    public Instant getCreatedAt() { return createdAt; }
    public Long getCreatedBy() { return createdBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getUpdatedBy() { return updatedBy; }
    public Instant getDeletedAt() { return deletedAt; }
    public Long getDeletedBy() { return deletedBy; }
}
