package com.pedidos.api_pedidos.controller;

import org.springframework.web.bind.annotation.*;

import com.pedidos.api_pedidos.dto.order_item.OrderItemRequest;
import com.pedidos.api_pedidos.dto.order_item.OrderItemResponse;
import com.pedidos.api_pedidos.service.OrderItemService;

import java.util.List;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService service;

    public OrderItemController(OrderItemService service) {
        this.service = service;
    }

    @PostMapping
    public OrderItemResponse create(@RequestBody OrderItemRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public OrderItemResponse update(@PathVariable Long id, @RequestBody OrderItemRequest request) {
        return service.update(id, request);
    }

    @GetMapping
    public List<OrderItemResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public OrderItemResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/by-order/{orderId}")
    public List<OrderItemResponse> getByOrderId(@PathVariable Long orderId) {
        return service.getByOrderId(orderId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
