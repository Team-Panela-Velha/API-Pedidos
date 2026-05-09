package com.pedidos.api_pedidos.service;

import com.pedidos.api_pedidos.domain.entity.ExtraEntity;
import com.pedidos.api_pedidos.dto.extra.ExtraRequest;
import com.pedidos.api_pedidos.dto.extra.ExtraResponse;
import com.pedidos.api_pedidos.exception.ResourceNotFoundException;
import com.pedidos.api_pedidos.repository.ExtraRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExtraService {

    private final ExtraRepository extraRepository;

    public ExtraService(ExtraRepository extraRepository) {
        this.extraRepository = extraRepository;
    }

    public ExtraResponse createExtra(ExtraRequest request) {
        validatePrice(request.getPrice());
        ExtraEntity extra = new ExtraEntity(request.getName(), request.getPrice());
        extra = extraRepository.save(extra);
        return new ExtraResponse(extra.getExtraId(), extra.getName(), extra.getPrice());
    }

    public List<ExtraResponse> getAllExtras() {
        return extraRepository.findAll()
                .stream()
                .map(e -> new ExtraResponse(e.getExtraId(), e.getName(), e.getPrice()))
                .collect(Collectors.toList());
    }

    public ExtraResponse getExtraById(UUID extraId) {
        ExtraEntity extra = extraRepository.findById(extraId)
                .orElseThrow(() -> new ResourceNotFoundException("Adicional não encontrado com o ID: " + extraId));
        return new ExtraResponse(extra.getExtraId(), extra.getName(), extra.getPrice());
    }

    public ExtraResponse updateExtra(UUID extraId, ExtraRequest request) {
        validatePrice(request.getPrice());
        
        ExtraEntity extra = extraRepository.findById(extraId)
                .orElseThrow(() -> new ResourceNotFoundException("Adicional não encontrado com o ID: " + extraId));
                
        extra.setName(request.getName());
        extra.setPrice(request.getPrice());
        
        extra = extraRepository.save(extra);
        return new ExtraResponse(extra.getExtraId(), extra.getName(), extra.getPrice());
    }

    public void deleteExtra(UUID extraId) {
        ExtraEntity extra = extraRepository.findById(extraId)
                .orElseThrow(() -> new ResourceNotFoundException("Adicional não encontrado com o ID: " + extraId));
        extraRepository.delete(extra);
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo ou nulo.");
        }
    }
}
