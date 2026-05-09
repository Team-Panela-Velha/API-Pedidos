package com.pedidos.api_pedidos.dto.category;

import java.util.UUID;

public class CategoryResponse {
    private UUID id;
    private String code;

    public CategoryResponse(UUID id, String code) {
        this.id = id;
        this.code = code;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }
}
