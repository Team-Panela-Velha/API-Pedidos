package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    List<PaymentEntity> findByTabId(Long tabId);
}
