package com.pedidos.api_pedidos.service;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.OrderEntity;
import com.pedidos.api_pedidos.domain.entity.OrderItemEntity;
import com.pedidos.api_pedidos.domain.entity.ProductEntity;
import com.pedidos.api_pedidos.dto.extra.ExtraResponse;
import com.pedidos.api_pedidos.dto.order_item.OrderItemRequest;
import com.pedidos.api_pedidos.dto.order_item.OrderItemResponse;
import com.pedidos.api_pedidos.repository.ItemExtraRepository;
import com.pedidos.api_pedidos.repository.OrderItemRepository;
import com.pedidos.api_pedidos.repository.OrderRepository;
import com.pedidos.api_pedidos.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemService {

    private final OrderItemRepository repository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ItemExtraRepository itemExtraRepository;

    public OrderItemService(OrderItemRepository repository,
                            OrderRepository orderRepository,
                            ProductRepository productRepository,
                            ItemExtraRepository itemExtraRepository) {
        this.repository = repository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.itemExtraRepository = itemExtraRepository;
    }

    public OrderItemResponse create(OrderItemRequest request) {
        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderItemEntity entity = new OrderItemEntity(
                product, order, request.getQuantity(), request.getObservation());
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public OrderItemResponse update(Long id, OrderItemRequest request) {
        OrderItemEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        entity.setProduct(product);
        entity.setOrder(order);
        entity.setQuantity(request.getQuantity());
        entity.setObservation(request.getObservation());
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public List<OrderItemResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OrderItemResponse> getByOrderId(Long orderId) {
        return repository.findByOrderId(orderId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderItemResponse getById(Long id) {
        OrderItemEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));
        return toResponse(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private OrderItemResponse toResponse(OrderItemEntity entity) {
        Long productId = entity.getProduct() != null ? entity.getProduct().getId() : null;
        String productName = entity.getProduct() != null ? entity.getProduct().getName() : null;
        Long orderId = entity.getOrder() != null ? entity.getOrder().getId() : null;

        List<ExtraResponse> extras = itemExtraRepository.findByOrderItemId(entity.getId())
                .stream()
                .map(ie -> new ExtraResponse(
                        ie.getExtra().getId(),
                        ie.getExtra().getName(),
                        ie.getExtra().getPrice()))
                .collect(Collectors.toList());

        return new OrderItemResponse(
                entity.getId(), productId, productName, orderId,
                entity.getQuantity(), entity.getObservation(), extras);
    }
}