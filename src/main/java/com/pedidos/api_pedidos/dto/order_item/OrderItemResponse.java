package com.pedidos.api_pedidos.dto.order_item;

import com.pedidos.api_pedidos.dto.extra.ExtraResponse;

import java.util.List;

public class OrderItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Long orderId;
    private Short quantity;
    private String observation;
    private List<ExtraResponse> extras;

    public OrderItemResponse(Long id, Long productId, String productName, Long orderId,
                             Short quantity, String observation, List<ExtraResponse> extras) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.orderId = orderId;
        this.quantity = quantity;
        this.observation = observation;
        this.extras = extras;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Long getOrderId() { return orderId; }
    public Short getQuantity() { return quantity; }
    public String getObservation() { return observation; }
    public List<ExtraResponse> getExtras() { return extras; }
}
