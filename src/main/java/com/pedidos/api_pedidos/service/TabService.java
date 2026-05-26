package com.pedidos.api_pedidos.service;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.TabEntity;
import com.pedidos.api_pedidos.domain.entity.TableEntity;
import com.pedidos.api_pedidos.dto.tab.StartTabRequest;
import com.pedidos.api_pedidos.dto.tab.TabRequest;
import com.pedidos.api_pedidos.dto.tab.TabResponse;
import com.pedidos.api_pedidos.repository.TabRepository;
import com.pedidos.api_pedidos.repository.TableRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TabService {

    private final TabRepository repository;
    private final TableRepository tableRepository;

    public TabService(TabRepository repository, TableRepository tableRepository) {
        this.repository = repository;
        this.tableRepository = tableRepository;
    }

    // ── Endpoints solicitados ─────────────────────────────────────────────────

    /**
     * Cria uma nova comanda a partir do código da mesa.
     * Lança exceção se já existir uma comanda aberta para a mesma mesa.
     */
    public TabResponse startTab(StartTabRequest request) {
        TableEntity table = tableRepository.findByCode(request.getTableCode())
                .orElseThrow(() -> new RuntimeException("Table not found: " + request.getTableCode()));

        repository.findByTableIdAndClosedFalse(table.getId()).ifPresent(existing -> {
            throw new RuntimeException("Table " + request.getTableCode() + " already has an open tab (id=" + existing.getId() + ")");
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
                .orElseThrow(() -> new RuntimeException("Tab not found"));

        if (Boolean.TRUE.equals(entity.getClosed())) {
            throw new RuntimeException("Tab is already closed");
        }

        entity.setClosed(true);
        entity = repository.save(entity);

        return toResponse(entity);
    }

    // ── CRUD padrão ───────────────────────────────────────────────────────────

    public TabResponse create(TabRequest request) {
        TableEntity table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));

        TabEntity entity = new TabEntity(request.getTotalValue(), table);
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public TabResponse update(Long id, TabRequest request) {
        TabEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tab not found"));

        TableEntity table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));

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
                .orElseThrow(() -> new RuntimeException("Tab not found"));
        return toResponse(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private TabResponse toResponse(TabEntity entity) {
        Long tableId = entity.getTable() != null ? entity.getTable().getId() : null;
        String tableCode = entity.getTable() != null ? entity.getTable().getCode() : null;
        return new TabResponse(entity.getId(), entity.getTotalValue(), entity.getClosed(), tableId, tableCode);
    }
}
