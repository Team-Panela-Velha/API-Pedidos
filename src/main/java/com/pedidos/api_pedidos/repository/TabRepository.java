package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.TabEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TabRepository extends JpaRepository<TabEntity, Long> {
    Optional<TabEntity> findByTableIdAndClosedFalse(Long tableId);
}
