package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.ItemExtraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemExtraRepository extends JpaRepository<ItemExtraEntity, Long> {
    List<ItemExtraEntity> findByOrderItemId(Long orderItemId);
}
