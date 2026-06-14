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

    /**
     * POST /orders
     * Recebe lista de itens (product_id, quantity, observation, extra_ids)
     * Valida que a comanda existe e está OPEN; retorna 409 se não estiver
     * Para cada item persiste unit_price_snapshot = product.price
     * Após criar chama FcmService.notifyKitchen(orderId)
     * Chama TabService.recalculateTotalValue(tabId) ao final
     */
    @PostMapping
    public OrderResponse createOrderWithItems(@RequestBody CreateOrderRequest request) {
        return service.createOrderWithItems(request);
    }

    /**
     * GET /orders/tab/{tabId}
     * Lista pedidos da comanda
     */
    @GetMapping("/tab/{tabId}")
    public List<OrderResponse> getTabOrders(@PathVariable Long tabId) {
        return service.getTabOrders(tabId);
    }

    /**
     * GET /orders/{id}
     * Detalhe com itens e extras
     */
    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /**
     * PUT /orders/{id}/status
     * Sequência obrigatória: RECEIVED → IN_PREPARATION → READY → DELIVERED
     * Fora da sequência retorna 422
     * Após atualizar chama FcmService.notifyTable(tableId, status)
     */
    @PutMapping("/{id}/status")
    public void updateOrderStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusRequest request) {
        service.updateOrderStatus(id, request);
    }

    /**
     * DELETE /orders/{id}
     * Só permitido se status = RECEIVED; caso contrário retorna 409
     * Após cancelar chama TabService.recalculateTotalValue(tabId)
     */
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        service.deleteOrder(id);
    }

    // ── CRUD padrão (legado) ────────────────────────────────────────────────

    @PutMapping("/{id}")
    public OrderResponse update(@PathVariable Long id, @RequestBody OrderRequest request) {
        return service.update(id, request);
    }

    @GetMapping
    public List<OrderResponse> getAll() {
        return service.getAll();
    }
}
