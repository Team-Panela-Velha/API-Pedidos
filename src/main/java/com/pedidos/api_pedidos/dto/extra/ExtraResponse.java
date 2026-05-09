package com.pedidos.api_pedidos.dto.extra;

import java.math.BigDecimal;
import java.util.UUID;

public class ExtraResponse {
    private UUID extraId;
    private String name;
    private BigDecimal price;

    public ExtraResponse(UUID extraId, String name, BigDecimal price) {
        this.extraId = extraId;
        this.name = name;
        this.price = price;
    }

    public UUID getExtraId() {
        return extraId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
