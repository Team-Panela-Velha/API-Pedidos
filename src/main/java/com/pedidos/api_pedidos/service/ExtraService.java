package com.pedidos.api_pedidos.service;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.ExtraEntity;
import com.pedidos.api_pedidos.dto.extra.ExtraRequest;
import com.pedidos.api_pedidos.dto.extra.ExtraResponse;
import com.pedidos.api_pedidos.exception.ConflictException;
import com.pedidos.api_pedidos.exception.ResourceNotFoundException;
import com.pedidos.api_pedidos.repository.ExtraRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExtraService {

    private final ExtraRepository repository;

    public ExtraService(ExtraRepository repository) {
        this.repository = repository;
    }

    public ExtraResponse create(ExtraRequest request) {
        ExtraEntity entity = new ExtraEntity(request.getName(), request.getPrice());
        entity = repository.save(entity);
        return toResponse(entity);
    }

    public ExtraResponse update(Long id, ExtraRequest request) {
        ExtraEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Extra não encontrado: " + id));

        entity.setName(request.getName());
        entity.setPrice(request.getPrice());
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public List<ExtraResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ExtraResponse getById(Long id) {
        ExtraEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Extra não encontrado: " + id));
        return toResponse(entity);
    }


    public void delete(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Extra não encontrado: " + id));

        if (repository.isExtraInOpenTab(id)) {
            throw new ConflictException("Não é possível remover: extra está sendo usado em uma comanda aberta");
        }

        repository.deleteById(id);
    }

    private ExtraResponse toResponse(ExtraEntity entity) {
        return new ExtraResponse(entity.getId(), entity.getName(), entity.getPrice());
    }
}
