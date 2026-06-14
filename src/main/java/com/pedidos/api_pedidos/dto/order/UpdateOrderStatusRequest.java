package com.pedidos.api_pedidos.dto.order;

import com.pedidos.api_pedidos.domain.enums.OrderStatus;

public class UpdateOrderStatusRequest {
    private OrderStatus status;

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
