package com.pedidos.api_pedidos.service;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.OrderEntity;
import com.pedidos.api_pedidos.domain.entity.TabEntity;
import com.pedidos.api_pedidos.dto.extra.ExtraResponse;
import com.pedidos.api_pedidos.dto.order.OrderRequest;
import com.pedidos.api_pedidos.dto.order.OrderResponse;
import com.pedidos.api_pedidos.dto.order_item.OrderItemResponse;
import com.pedidos.api_pedidos.repository.ItemExtraRepository;
import com.pedidos.api_pedidos.repository.OrderItemRepository;
import com.pedidos.api_pedidos.repository.OrderRepository;
import com.pedidos.api_pedidos.repository.TabRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final TabRepository tabRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemExtraRepository itemExtraRepository;

    public OrderService(OrderRepository repository,
                        TabRepository tabRepository,
                        OrderItemRepository orderItemRepository,
                        ItemExtraRepository itemExtraRepository) {
        this.repository = repository;
        this.tabRepository = tabRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemExtraRepository = itemExtraRepository;
    }

    /**
     * Retorna todos os pedidos de uma comanda, com seus itens e extras aninhados.
     */
    public List<OrderResponse> getTabOrders(Long tabId) {
        tabRepository.findById(tabId)
                .orElseThrow(() -> new RuntimeException("Tab not found"));

        return repository.findByTabId(tabId)
                .stream()
                .map(this::toDetailedResponse)
                .collect(Collectors.toList());
    }

    // ── CRUD padrão ───────────────────────────────────────────────────────────

    public OrderResponse create(OrderRequest request) {
        TabEntity tab = tabRepository.findById(request.getTabId())
                .orElseThrow(() -> new RuntimeException("Tab not found"));

        if (Boolean.TRUE.equals(tab.getClosed())) {
            throw new RuntimeException("Cannot add orders to a closed tab");
        }

        OrderEntity entity = new OrderEntity(tab);
        entity = repository.save(entity);

        return toDetailedResponse(entity);
    }

    public OrderResponse update(Long id, OrderRequest request) {
        OrderEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        TabEntity tab = tabRepository.findById(request.getTabId())
                .orElseThrow(() -> new RuntimeException("Tab not found"));

        entity.setTab(tab);
        entity = repository.save(entity);

        return toDetailedResponse(entity);
    }

    public List<OrderResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDetailedResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getById(Long id) {
        OrderEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toDetailedResponse(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private OrderResponse toDetailedResponse(OrderEntity order) {
        List<OrderItemResponse> items = orderItemRepository.findByOrderId(order.getId())
                .stream()
                .map(item -> {
                    List<ExtraResponse> extras = itemExtraRepository.findByOrderItemId(item.getId())
                            .stream()
                            .map(ie -> new ExtraResponse(
                                    ie.getExtra().getId(),
                                    ie.getExtra().getName(),
                                    ie.getExtra().getPrice()))
                            .collect(Collectors.toList());

                    String productName = item.getProduct() != null ? item.getProduct().getName() : null;
                    Long productId = item.getProduct() != null ? item.getProduct().getId() : null;

                    return new OrderItemResponse(
                            item.getId(), productId, productName,
                            order.getId(), item.getQuantity(),
                            item.getObservation(), extras);
                })
                .collect(Collectors.toList());

        Long tabId = order.getTab() != null ? order.getTab().getId() : null;
        return new OrderResponse(order.getId(), tabId, items);
    }
}
