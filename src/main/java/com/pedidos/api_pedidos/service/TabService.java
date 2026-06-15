package com.pedidos.api_pedidos.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.OrderEntity;
import com.pedidos.api_pedidos.domain.entity.TabEntity;
import com.pedidos.api_pedidos.domain.entity.TableEntity;
import com.pedidos.api_pedidos.dto.tab.StartTabRequest;
import com.pedidos.api_pedidos.dto.tab.TabRequest;
import com.pedidos.api_pedidos.dto.tab.TabResponse;
import com.pedidos.api_pedidos.exception.ConflictException;
import com.pedidos.api_pedidos.exception.ResourceNotFoundException;
import com.pedidos.api_pedidos.repository.ItemExtraRepository;
import com.pedidos.api_pedidos.repository.OrderItemRepository;
import com.pedidos.api_pedidos.repository.OrderRepository;
import com.pedidos.api_pedidos.repository.TabRepository;
import com.pedidos.api_pedidos.repository.TableRepository;

@Service
public class TabService {

    private final TabRepository repository;
    private final TableRepository tableRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemExtraRepository itemExtraRepository;

    public TabService(TabRepository repository, TableRepository tableRepository,
                      OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                      ItemExtraRepository itemExtraRepository) {
        this.repository = repository;
        this.tableRepository = tableRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemExtraRepository = itemExtraRepository;
    }

    // ── Endpoints solicitados ─────────────────────────────────────────────────

    /**
     * Cria uma nova comanda a partir do código da mesa.
     * Lança exceção se já existir uma comanda aberta para a mesma mesa.
     */
    public TabResponse startTab(StartTabRequest request) {
        TableEntity table = tableRepository.findByCode(request.getTableCode())
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada: " + request.getTableCode()));

        repository.findByTableIdAndClosedFalse(table.getId()).ifPresent(existing -> {
            throw new ConflictException("A mesa " + request.getTableCode() + " já possui uma comanda aberta (id=" + existing.getId() + ")");
        });

        TabEntity entity = new TabEntity(BigDecimal.ZERO, table);
        entity = repository.save(entity);

        return toResponse(entity);
    }

    /**
     * Encerra a comanda. Após fechada, não pode receber mais pedidos.
     */
    public TabResponse closeTab(Long id) {
        TabEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada: " + id));

        if (Boolean.TRUE.equals(entity.getClosed())) {
            throw new ConflictException("A comanda já está fechada");
        }

        entity.setClosed(true);
        entity = repository.save(entity);

        return toResponse(entity);
    }

    // ── CRUD padrão ───────────────────────────────────────────────────────────

    public TabResponse create(TabRequest request) {
        TableEntity table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada: " + request.getTableId()));

        TabEntity entity = new TabEntity(request.getTotalValue(), table);
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public TabResponse update(Long id, TabRequest request) {
        TabEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada: " + id));

        TableEntity table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada: " + request.getTableId()));

        entity.setTotalValue(request.getTotalValue());
        entity.setTable(table);
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public List<TabResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TabResponse getById(Long id) {
        TabEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada: " + id));
        return toResponse(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    /**
     * Recalcula o valor total da comanda baseado em todos os itens e seus extras.
     */
    public void recalculateTotalValue(Long tabId) {
        TabEntity tab = repository.findById(tabId)
                .orElseThrow(() -> new ResourceNotFoundException("Comanda não encontrada: " + tabId));

        List<OrderEntity> orders = orderRepository.findByTabId(tabId);
        BigDecimal totalValue = BigDecimal.ZERO;

        for (OrderEntity order : orders) {
            List<com.pedidos.api_pedidos.domain.entity.OrderItemEntity> items = orderItemRepository.findByOrderId(order.getId());
            
            for (com.pedidos.api_pedidos.domain.entity.OrderItemEntity item : items) {
                // Adiciona (quantidade * preço unitário do snapshot)
                BigDecimal itemTotal = item.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalValue = totalValue.add(itemTotal);

                // Adiciona os extras
                List<com.pedidos.api_pedidos.domain.entity.ItemExtraEntity> extras = 
                    itemExtraRepository.findByOrderItemId(item.getId());
                for (com.pedidos.api_pedidos.domain.entity.ItemExtraEntity extra : extras) {
                    totalValue = totalValue.add(extra.getExtra().getPrice());
                }
            }
        }

        tab.setTotalValue(totalValue);
        repository.save(tab);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private TabResponse toResponse(TabEntity entity) {
        Long tableId = entity.getTable() != null ? entity.getTable().getId() : null;
        String tableCode = entity.getTable() != null ? entity.getTable().getCode() : null;
        return new TabResponse(entity.getId(), entity.getTotalValue(), entity.getClosed(), tableId, tableCode);
    }
}
