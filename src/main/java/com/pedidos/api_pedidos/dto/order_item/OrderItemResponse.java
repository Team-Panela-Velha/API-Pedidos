package com.pedidos.api_pedidos.dto.order_item;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.pedidos.api_pedidos.domain.enums.OrderStatus;
import com.pedidos.api_pedidos.dto.extra.ExtraResponse;

public class OrderItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private Long orderId;
    private Short quantity;
    private String observation;
    private BigDecimal unitPriceSnapshot;
    private OrderStatus status;
    private List<ExtraResponse> extras;
    private Instant createdAt;

    public OrderItemResponse(Long id, Long productId, String productName, String productImage, Long orderId,
                             Short quantity, String observation, BigDecimal unitPriceSnapshot, 
                             OrderStatus status, List<ExtraResponse> extras) {
        this(id, productId, productName, productImage, orderId, quantity, observation,
                unitPriceSnapshot, status, extras, null);
    }

    public OrderItemResponse(Long id, Long productId, String productName, String productImage, Long orderId,
                             Short quantity, String observation, BigDecimal unitPriceSnapshot,
                             OrderStatus status, List<ExtraResponse> extras, Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.orderId = orderId;
        this.quantity = quantity;
        this.observation = observation;
        this.unitPriceSnapshot = unitPriceSnapshot;
        this.status = status;
        this.extras = extras;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductImage() { return productImage; }
    public Long getOrderId() { return orderId; }
    public Short getQuantity() { return quantity; }
    public String getObservation() { return observation; }
    public BigDecimal getUnitPriceSnapshot() { return unitPriceSnapshot; }
    public OrderStatus getStatus() { return status; }
    public List<ExtraResponse> getExtras() { return extras; }
    public Instant getCreatedAt() { return createdAt; }
}
