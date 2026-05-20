package com.pedidos.api_pedidos.service;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.OrderEntity;
import com.pedidos.api_pedidos.domain.entity.TabEntity;
import com.pedidos.api_pedidos.dto.order.OrderRequest;
import com.pedidos.api_pedidos.dto.order.OrderResponse;
import com.pedidos.api_pedidos.repository.OrderRepository;
import com.pedidos.api_pedidos.repository.TabRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final TabRepository tabRepository;

    public OrderService(OrderRepository repository, TabRepository tabRepository) {
        this.repository = repository;
        this.tabRepository = tabRepository;
    }

    public OrderResponse create(OrderRequest request) {
        TabEntity tab = tabRepository.findById(request.getTabId())
                .orElseThrow(() -> new RuntimeException("Tab not found"));

        OrderEntity entity = new OrderEntity(tab);
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public OrderResponse update(Long id, OrderRequest request) {
        OrderEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        TabEntity tab = tabRepository.findById(request.getTabId())
                .orElseThrow(() -> new RuntimeException("Tab not found"));

        entity.setTab(tab);
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public List<OrderResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getById(Long id) {
        OrderEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toResponse(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private OrderResponse toResponse(OrderEntity entity) {
        Long tabId = entity.getTab() != null ? entity.getTab().getId() : null;
        return new OrderResponse(entity.getId(), tabId);
    }
}
