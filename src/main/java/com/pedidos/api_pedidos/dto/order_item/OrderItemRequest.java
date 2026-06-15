package com.pedidos.api_pedidos.dto.order_item;

import java.util.List;

public class OrderItemRequest {

    private Long productId;
    private Long orderId;
    private Short quantity;
    private String observation;
    private List<Long> extraIds;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Short getQuantity() { return quantity; }
    public void setQuantity(Short quantity) { this.quantity = quantity; }

    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }

    public List<Long> getExtraIds() { return extraIds; }
    public void setExtraIds(List<Long> extraIds) { this.extraIds = extraIds; }
}
