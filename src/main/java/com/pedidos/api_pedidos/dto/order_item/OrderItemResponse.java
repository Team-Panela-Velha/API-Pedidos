package com.pedidos.api_pedidos.dto.order_item;

public class OrderItemResponse {

    private Long id;
    private Long productId;
    private Long orderId;
    private Short quantity;
    private String observation;

    public OrderItemResponse(Long id, Long productId, Long orderId, Short quantity, String observation) {
        this.id = id;
        this.productId = productId;
        this.orderId = orderId;
        this.quantity = quantity;
        this.observation = observation;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public Long getOrderId() { return orderId; }
    public Short getQuantity() { return quantity; }
    public String getObservation() { return observation; }
}
