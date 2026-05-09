package com.pedidos.api_pedidos.dto.product;

import java.util.UUID;

public class ProductResponse {
    private UUID id;
    private String code;

    public ProductResponse(UUID id, String code) {
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
