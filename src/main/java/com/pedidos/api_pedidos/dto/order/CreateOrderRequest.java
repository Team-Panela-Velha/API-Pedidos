package com.pedidos.api_pedidos.dto.order;

import java.util.List;

import com.pedidos.api_pedidos.dto.order_item.OrderItemRequest;

public class CreateOrderRequest {
    private Long tabId;
    private String clientRequestId;
    private List<OrderItemRequest> items;

    public Long getTabId() { return tabId; }
    public void setTabId(Long tabId) { this.tabId = tabId; }
    public String getClientRequestId() { return clientRequestId; }
    public void setClientRequestId(String clientRequestId) { this.clientRequestId = clientRequestId; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}
