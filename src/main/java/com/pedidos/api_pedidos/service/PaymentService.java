package com.pedidos.api_pedidos.service;

import com.pedidos.api_pedidos.domain.entity.PaymentEntity;
import com.pedidos.api_pedidos.domain.entity.StaffUserEntity;
import com.pedidos.api_pedidos.domain.entity.TabEntity;
import com.pedidos.api_pedidos.dto.payment.PaymentRequest;
import com.pedidos.api_pedidos.dto.payment.PaymentResponse;
import com.pedidos.api_pedidos.exception.UnprocessableException;
import com.pedidos.api_pedidos.repository.PaymentRepository;
import com.pedidos.api_pedidos.repository.StaffUserRepository;
import com.pedidos.api_pedidos.repository.TabRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository repository;
    private final TabRepository tabRepository;
    private final StaffUserRepository staffUserRepository;

    public PaymentService(PaymentRepository repository,
                          TabRepository tabRepository,
                          StaffUserRepository staffUserRepository) {
        this.repository = repository;
        this.tabRepository = tabRepository;
        this.staffUserRepository = staffUserRepository;
    }

    /**
     * Registra um pagamento para a comanda. Múltiplos pagamentos por comanda são permitidos.
     * Valida amount > 0 e existência da comanda (senão 422).
     *
     * @param staffUserId id do staff autenticado (garçom) extraído do JWT; pode ser null.
     */
    @Transactional
    public PaymentResponse create(PaymentRequest request, Long staffUserId) {
        if (request.getMethod() == null) {
            throw new UnprocessableException("Método de pagamento é obrigatório");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UnprocessableException("O valor do pagamento deve ser maior que zero");
        }

        TabEntity tab = tabRepository.findById(request.getTabId())
                .orElseThrow(() -> new UnprocessableException("Comanda não encontrada: " + request.getTabId()));

        StaffUserEntity registeredBy = staffUserId == null
                ? null
                : staffUserRepository.findById(staffUserId).orElse(null);

        PaymentEntity entity = new PaymentEntity(
                tab,
                request.getMethod(),
                request.getAmount(),
                Instant.now(),
                registeredBy
        );
        entity = repository.save(entity);

        return toResponse(entity);
    }

    public List<PaymentResponse> getByTab(Long tabId) {
        return repository.findByTabId(tabId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse toResponse(PaymentEntity entity) {
        Long tabId = entity.getTab() != null ? entity.getTab().getId() : null;
        return new PaymentResponse(
                entity.getId(),
                tabId,
                entity.getPaymentMethod(),
                entity.getAmount(),
                entity.getPaidAt()
        );
    }
}
