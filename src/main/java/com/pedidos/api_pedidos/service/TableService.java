package com.pedidos.api_pedidos.service;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.TableEntity;
import com.pedidos.api_pedidos.dto.table.TableRequest;
import com.pedidos.api_pedidos.dto.table.TableResponse;
import com.pedidos.api_pedidos.repository.TableRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableService {

    private final TableRepository repository;

    public TableService(TableRepository repository) {
        this.repository = repository;
    }

    public TableResponse create(TableRequest request) {
        TableEntity entity = new TableEntity(request.getCode());
        entity = repository.save(entity);

        return new TableResponse(entity.getId(), entity.getCode());
    }

    public TableResponse update(Long id, TableRequest request) {
        TableEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        entity.setCode(request.getCode());

        entity = repository.save(entity);

        return new TableResponse(entity.getId(), entity.getCode());
    }

    public List<TableResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(e -> new TableResponse(e.getId(), e.getCode()))
                .collect(Collectors.toList());
    }

    public TableResponse getById(Long id) {
        TableEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        return new TableResponse(entity.getId(), entity.getCode());
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}