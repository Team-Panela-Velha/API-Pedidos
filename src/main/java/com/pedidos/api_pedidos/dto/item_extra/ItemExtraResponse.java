package com.pedidos.api_pedidos.dto.item_extra;

import java.time.Instant;

public class ItemExtraResponse {

    private Long id;
    private Long orderItemId;
    private Long extraId;
    private Instant createdAt;

    public ItemExtraResponse(Long id, Long orderItemId, Long extraId, Instant createdAt) {
        this.id = id;
        this.orderItemId = orderItemId;
        this.extraId = extraId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getOrderItemId() { return orderItemId; }
    public Long getExtraId() { return extraId; }
    public Instant getCreatedAt() { return createdAt; }
}
