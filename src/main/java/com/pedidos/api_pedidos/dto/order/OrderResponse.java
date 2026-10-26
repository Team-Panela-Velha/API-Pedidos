package com.pedidos.api_pedidos.dto.order;

import com.pedidos.api_pedidos.dto.order_item.OrderItemResponse;

import java.util.List;
import java.time.Instant;

public class OrderResponse {

    private Long id;
    private Long tabId;
    private List<OrderItemResponse> items;
    private Instant createdAt;

    public OrderResponse(Long id, Long tabId, List<OrderItemResponse> items) {
        this(id, tabId, items, null);
    }

    public OrderResponse(Long id, Long tabId, List<OrderItemResponse> items, Instant createdAt) {
        this.id = id;
        this.tabId = tabId;
        this.items = items;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getTabId() { return tabId; }
    public List<OrderItemResponse> getItems() { return items; }
    public Instant getCreatedAt() { return createdAt; }
}
