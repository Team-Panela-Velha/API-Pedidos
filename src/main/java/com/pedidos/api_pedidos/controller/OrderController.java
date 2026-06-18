package com.pedidos.api_pedidos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pedidos.api_pedidos.dto.order.CreateOrderRequest;
import com.pedidos.api_pedidos.dto.order.OrderRequest;
import com.pedidos.api_pedidos.dto.order.OrderResponse;
import com.pedidos.api_pedidos.dto.order.UpdateOrderStatusRequest;
import com.pedidos.api_pedidos.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public OrderResponse createOrderWithItems(@RequestBody CreateOrderRequest request) {
        return service.createOrderWithItems(request);
    }

    @GetMapping("/tab/{tabId}")
    public List<OrderResponse> getTabOrders(@PathVariable Long tabId) {
        return service.getTabOrders(tabId);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}/status")
    public void updateOrderStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusRequest request) {
        service.updateOrderStatus(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        service.deleteOrder(id);
    }

    @PutMapping("/{id}")
    public OrderResponse update(@PathVariable Long id, @RequestBody OrderRequest request) {
        return service.update(id, request);
    }

    @GetMapping
    public List<OrderResponse> getAll() {
        return service.getAll();
    }
}
