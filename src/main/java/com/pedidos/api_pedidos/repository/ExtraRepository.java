package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.ExtraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExtraRepository extends JpaRepository<ExtraEntity, Long> {


    @Query("SELECT COUNT(ie) > 0 FROM ItemExtraEntity ie JOIN ie.orderItem oi JOIN oi.order o JOIN o.tab t WHERE ie.extra.id = :extraId AND t.closed = false")
    boolean isExtraInOpenTab(@Param("extraId") Long extraId);
}
