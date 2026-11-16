package com.pedidos.api_pedidos.controller;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pedidos.api_pedidos.dto.order.CreateOrderRequest;
import com.pedidos.api_pedidos.dto.order.OrderRequest;
import com.pedidos.api_pedidos.dto.order.OrderResponse;
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
        return service.create(request);
    }

    @GetMapping("/tab/{tabId}")
    public List<OrderResponse> getTabOrders(@PathVariable Long tabId) {
        return service.getTabOrders(tabId);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // TODO
    // @PutMapping("/{id}/status")
    // public void updateOrderStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusRequest request) {
    //     service.updateOrderStatus(id, request);
    // }

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

    @GetMapping("/kitchen")
    public org.springframework.data.domain.Page<OrderResponse> getKitchenOrders(
            @RequestParam(required = false) String delivered,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Boolean deliveredFilter;
        if (delivered == null || delivered.isBlank() || "false".equalsIgnoreCase(delivered)) {
            deliveredFilter = Boolean.FALSE;
        } else if ("true".equalsIgnoreCase(delivered)) {
            deliveredFilter = Boolean.TRUE;
        } else if ("all".equalsIgnoreCase(delivered)) {
            deliveredFilter = null;
        } else {
            throw new IllegalArgumentException("O filtro delivered deve ser true, false ou all");
        }
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        Sort sort = deliveredFilter == null
                ? Sort.by(Sort.Direction.ASC, "delivered", "createdAt", "id")
                : Sort.by(Boolean.TRUE.equals(deliveredFilter) ? Sort.Direction.DESC : Sort.Direction.ASC, "createdAt")
                        .and(Sort.by(Sort.Direction.ASC, "id"));
        return service.getKitchenOrders(deliveredFilter, PageRequest.of(safePage, safeSize, sort));
    }

    @PatchMapping("/{id}/delivered")
    public OrderResponse markDelivered(@PathVariable Long id) {
        return service.markDelivered(id);
    }
}
