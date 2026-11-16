package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByTabId(Long tabId);
    Optional<OrderEntity> findByClientRequestId(String clientRequestId);
    Page<OrderEntity> findByDelivered(Boolean delivered, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrderEntity o where o.id = :id")
    Optional<OrderEntity> findByIdForUpdate(@Param("id") Long id);
    long countByTabIdAndDeliveredFalse(Long tabId);

    @Query("select count(o) from OrderEntity o where o.tab.id = :tabId")
    long countByTabId(Long tabId);
}
