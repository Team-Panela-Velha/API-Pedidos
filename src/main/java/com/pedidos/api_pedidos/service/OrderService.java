package com.pedidos.api_pedidos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pedidos.api_pedidos.domain.entity.ExtraEntity;
import com.pedidos.api_pedidos.domain.entity.ItemExtraEntity;
import com.pedidos.api_pedidos.domain.entity.OrderEntity;
import com.pedidos.api_pedidos.domain.entity.OrderItemEntity;
import com.pedidos.api_pedidos.domain.entity.ProductEntity;
import com.pedidos.api_pedidos.domain.entity.TabEntity;
import com.pedidos.api_pedidos.domain.enums.OrderStatus;
import com.pedidos.api_pedidos.dto.extra.ExtraResponse;
import com.pedidos.api_pedidos.dto.order.CreateOrderRequest;
import com.pedidos.api_pedidos.dto.order.OrderRequest;
import com.pedidos.api_pedidos.dto.order.OrderResponse;
import com.pedidos.api_pedidos.dto.order_item.OrderItemRequest;
import com.pedidos.api_pedidos.dto.order_item.OrderItemResponse;
import com.pedidos.api_pedidos.repository.ExtraRepository;
import com.pedidos.api_pedidos.repository.ItemExtraRepository;
import com.pedidos.api_pedidos.repository.OrderItemRepository;
import com.pedidos.api_pedidos.repository.OrderRepository;
import com.pedidos.api_pedidos.repository.ProductExtraRepository;
import com.pedidos.api_pedidos.repository.ProductRepository;
import com.pedidos.api_pedidos.repository.TabRepository;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final TabRepository tabRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemExtraRepository itemExtraRepository;
    private final ProductRepository productRepository;
    private final ExtraRepository extraRepository;
    private final FcmService fcmService;
    private final TabService tabService;

    public OrderService(OrderRepository repository,
                        TabRepository tabRepository,
                        OrderItemRepository orderItemRepository,
                        ItemExtraRepository itemExtraRepository,
                        ProductRepository productRepository,
                        ExtraRepository extraRepository,
                        ProductExtraRepository productExtraRepository,
                        FcmService fcmService,
                        TabService tabService) {
        this.repository = repository;
        this.tabRepository = tabRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemExtraRepository = itemExtraRepository;
        this.productRepository = productRepository;
        this.extraRepository = extraRepository;
        this.fcmService = fcmService;
        this.tabService = tabService;
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
    
//     public void updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
//         List<OrderItemEntity> items = orderItemRepository.findByOrderId(orderId);
//         if (items.isEmpty()) {
//             throw new RuntimeException("Order has no items");
//         }

//         OrderEntity order = repository.findById(orderId)
//                 .orElseThrow(() -> new RuntimeException("Order not found"));

//         OrderStatus newStatus = request.getStatus();

//         for (OrderItemEntity item : items) {
//             OrderStatus currentStatus = item.getStatus();

//             // Valida a sequência: RECEIVED → IN_PREPARATION → READY → DELIVERED
//             boolean validTransition = false;
//             if (currentStatus == OrderStatus.RECEIVED && newStatus == OrderStatus.IN_PREPARATION) {
//                 validTransition = true;
//             } else if (currentStatus == OrderStatus.IN_PREPARATION && newStatus == OrderStatus.READY) {
//                 validTransition = true;
//             } else if (currentStatus == OrderStatus.READY && newStatus == OrderStatus.DELIVERED) {
//                 validTransition = true;
//             }

//             if (!validTransition) {
//                 throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
//                         "Invalid order status transition from " + currentStatus + " to " + newStatus);
//             }

//             item.setStatus(newStatus);
//             orderItemRepository.save(item);
//         }

//         // Chama notifyTable com o tableId
//         Long tableId = order.getTab() != null ? order.getTab().getTable().getId() : null;
//         if (tableId != null) {
//             fcmService.notifyTable(tableId, newStatus);
//         }
//     }

    public void deleteOrder(Long orderId) {
        OrderEntity order = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderItemEntity> items = orderItemRepository.findByOrderId(orderId);

        // Valida que todos os itens estão em RECEIVED
        for (OrderItemEntity item : items) {
            if (item.getStatus() != OrderStatus.RECEIVED) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Cannot delete order with items not in RECEIVED status");
            }
        }

        // Deleta items extras primeiro
        for (OrderItemEntity item : items) {
            List<ItemExtraEntity> extras = itemExtraRepository.findByOrderItemId(item.getId());
            itemExtraRepository.deleteAll(extras);
            orderItemRepository.delete(item);
        }

        // Deleta a ordem
        Long tabId = order.getTab().getId();
        repository.delete(order);

        // Recalcula o total da comanda
        tabService.recalculateTotalValue(tabId);

        // Notifica garçons sobre a remoção do pedido
        fcmService.notifyWaiter(tabId);
    }

    // ── Cria pedido com itens e extras ────────────────────────────────────────

    public OrderResponse create(CreateOrderRequest request) {
        TabEntity tab = tabRepository.findById(request.getTabId())
                .orElseThrow(() -> new RuntimeException("Tab not found"));

        if (Boolean.TRUE.equals(tab.getClosed())) {
            throw new RuntimeException("Cannot add orders to a closed tab");
        }

        OrderEntity order = new OrderEntity(tab);
        order = repository.save(order);

        List<OrderItemRequest> itemRequests = request.getItems();
        if (itemRequests != null && !itemRequests.isEmpty()) {
            for (OrderItemRequest itemReq : itemRequests) {
                ProductEntity product = productRepository.findById(itemReq.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.getProductId()));

                OrderItemEntity item = new OrderItemEntity(
                        product, order, itemReq.getQuantity(),
                        itemReq.getObservation(), product.getPrice());
                item = orderItemRepository.save(item);

                List<Long> extraIds = itemReq.getExtraIds();
                if (extraIds != null && !extraIds.isEmpty()) {
                    for (Long extraId : extraIds) {
                        ExtraEntity extra = extraRepository.findById(extraId)
                                .orElseThrow(() -> new RuntimeException("Extra not found: " + extraId));
                        ItemExtraEntity itemExtra = new ItemExtraEntity(item, extra);
                        itemExtraRepository.save(itemExtra);
                    }
                }
            }
        }

        tabService.recalculateTotalValue(tab.getId());
        fcmService.notifyKitchen(order.getId());
        fcmService.notifyWaiter(tab.getId());

        return toDetailedResponse(order);
    }

    // ── CRUD padrão (legado) ──────────────────────────────────────────────────

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
                    String productImage = item.getProduct() != null ? item.getProduct().getImage() : null;
                    Long productId = item.getProduct() != null ? item.getProduct().getId() : null;

                    return new OrderItemResponse(
                            item.getId(), productId, productName, productImage,
                            order.getId(), item.getQuantity(),
                            item.getObservation(), item.getUnitPriceSnapshot(),
                            item.getStatus(), extras);
                })
                .collect(Collectors.toList());

        Long tabId = order.getTab() != null ? order.getTab().getId() : null;
        return new OrderResponse(order.getId(), tabId, items);
    }
}
