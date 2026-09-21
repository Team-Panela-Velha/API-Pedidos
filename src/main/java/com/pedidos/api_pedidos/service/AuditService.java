package com.pedidos.api_pedidos.service;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditorAware<Long> auditorAware;

    public AuditService(AuditorAware<Long> auditorAware) {
        this.auditorAware = auditorAware;
    }

    public Long currentUserId() {
        return auditorAware.getCurrentAuditor()
                .orElseThrow(() -> new AccessDeniedException("Usuário autenticado necessário para excluir o registro"));
    }
}
