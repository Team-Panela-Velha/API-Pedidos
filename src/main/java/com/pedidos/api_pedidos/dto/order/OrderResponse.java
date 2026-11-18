package com.pedidos.api_pedidos.dto.order;

import com.pedidos.api_pedidos.dto.order_item.OrderItemResponse;

import java.util.List;
import java.time.Instant;
import com.pedidos.api_pedidos.domain.enums.OrderStatus;

public class OrderResponse {

    private Long id;
    private Long tabId;
    private List<OrderItemResponse> items;
    private Instant createdAt;
    private Boolean delivered;
    private Instant deliveredAt;
    private String tableCode;
    private Integer tableNumber;
    private Boolean tabClosed;
    private OrderStatus status;

    public OrderResponse(Long id, Long tabId, List<OrderItemResponse> items) {
        this(id, tabId, items, null);
    }

    public OrderResponse(Long id, Long tabId, List<OrderItemResponse> items, Instant createdAt) {
        this(id, tabId, items, createdAt, false, null, null, null, null);
    }

    public OrderResponse(Long id, Long tabId, List<OrderItemResponse> items, Instant createdAt,
                         Boolean delivered, Instant deliveredAt, String tableCode,
                         Boolean tabClosed, OrderStatus status) {
        this.id = id;
        this.tabId = tabId;
        this.items = items;
        this.createdAt = createdAt;
        this.delivered = delivered;
        this.deliveredAt = deliveredAt;
        this.tableCode = tableCode;
        this.tabClosed = tabClosed;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getTabId() { return tabId; }
    public List<OrderItemResponse> getItems() { return items; }
    public Instant getCreatedAt() { return createdAt; }
    public Boolean getDelivered() { return delivered; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public String getTableCode() { return tableCode; }
    public Integer getTableNumber() { return tableNumber; }
    public void setTableNumber(Integer tableNumber) { this.tableNumber = tableNumber; }
    public Boolean getTabClosed() { return tabClosed; }
    public OrderStatus getStatus() { return status; }
}
