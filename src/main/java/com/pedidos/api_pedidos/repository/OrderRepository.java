package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByTabId(Long tabId);
    Optional<OrderEntity> findByClientRequestId(String clientRequestId);
}
