package com.pedidos.api_pedidos.dto.item_extra;

public class ItemExtraRequest {

    private Long orderItemId;
    private Long extraId;

    public Long getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }

    public Long getExtraId() { return extraId; }
    public void setExtraId(Long extraId) { this.extraId = extraId; }
}
