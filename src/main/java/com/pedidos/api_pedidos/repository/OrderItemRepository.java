package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    List<OrderItemEntity> findByOrderId(Long orderId);
}
