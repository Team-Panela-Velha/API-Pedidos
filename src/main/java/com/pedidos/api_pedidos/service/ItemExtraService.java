package com.pedidos.api_pedidos.service;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.ExtraEntity;
import com.pedidos.api_pedidos.domain.entity.ItemExtraEntity;
import com.pedidos.api_pedidos.domain.entity.OrderItemEntity;
import com.pedidos.api_pedidos.dto.item_extra.ItemExtraRequest;
import com.pedidos.api_pedidos.dto.item_extra.ItemExtraResponse;
import com.pedidos.api_pedidos.repository.ExtraRepository;
import com.pedidos.api_pedidos.repository.ItemExtraRepository;
import com.pedidos.api_pedidos.repository.OrderItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemExtraService {

    private final ItemExtraRepository repository;
    private final OrderItemRepository orderItemRepository;
    private final ExtraRepository extraRepository;

    public ItemExtraService(ItemExtraRepository repository,
                            OrderItemRepository orderItemRepository,
                            ExtraRepository extraRepository) {
        this.repository = repository;
        this.orderItemRepository = orderItemRepository;
        this.extraRepository = extraRepository;
    }

    public ItemExtraResponse create(ItemExtraRequest request) {
        OrderItemEntity orderItem = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));
        ExtraEntity extra = extraRepository.findById(request.getExtraId())
                .orElseThrow(() -> new RuntimeException("Extra not found"));

        ItemExtraEntity entity = new ItemExtraEntity(orderItem, extra);
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public List<ItemExtraResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ItemExtraResponse> getByOrderItemId(Long orderItemId) {
        return repository.findByOrderItemId(orderItemId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ItemExtraResponse getById(Long id) {
        ItemExtraEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ItemExtra not found"));
        return toResponse(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private ItemExtraResponse toResponse(ItemExtraEntity entity) {
        Long orderItemId = entity.getOrderItem() != null ? entity.getOrderItem().getId() : null;
        Long extraId = entity.getExtra() != null ? entity.getExtra().getId() : null;
        return new ItemExtraResponse(entity.getId(), orderItemId, extraId);
    }
}
