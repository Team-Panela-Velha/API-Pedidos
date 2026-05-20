package com.pedidos.api_pedidos.dto.order;

public class OrderResponse {

    private Long id;
    private Long tabId;

    public OrderResponse(Long id, Long tabId) {
        this.id = id;
        this.tabId = tabId;
    }

    public Long getId() { return id; }
    public Long getTabId() { return tabId; }
}
