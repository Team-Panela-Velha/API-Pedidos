package com.pedidos.api_pedidos.dto.order;

import com.pedidos.api_pedidos.dto.order_item.OrderItemResponse;

import java.util.List;

public class OrderResponse {

    private Long id;
    private Long tabId;
    private List<OrderItemResponse> items;

    public OrderResponse(Long id, Long tabId, List<OrderItemResponse> items) {
        this.id = id;
        this.tabId = tabId;
        this.items = items;
    }

    public Long getId() { return id; }
    public Long getTabId() { return tabId; }
    public List<OrderItemResponse> getItems() { return items; }
}
