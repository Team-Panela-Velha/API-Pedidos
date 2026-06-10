package com.pedidos.api_pedidos.dto.item_extra;

public class ItemExtraResponse {

    private Long id;
    private Long orderItemId;
    private Long extraId;

    public ItemExtraResponse(Long id, Long orderItemId, Long extraId) {
        this.id = id;
        this.orderItemId = orderItemId;
        this.extraId = extraId;
    }

    public Long getId() { return id; }
    public Long getOrderItemId() { return orderItemId; }
    public Long getExtraId() { return extraId; }
}
