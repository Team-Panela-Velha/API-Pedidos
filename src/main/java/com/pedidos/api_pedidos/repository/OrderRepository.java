package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByTabId(Long tabId);
}
