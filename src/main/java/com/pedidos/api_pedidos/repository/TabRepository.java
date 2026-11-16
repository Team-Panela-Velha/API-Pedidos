package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.TabEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TabRepository extends JpaRepository<TabEntity, Long> {
    Optional<TabEntity> findByTableIdAndClosedFalse(Long tableId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TabEntity t where t.id = :id")
    Optional<TabEntity> findByIdForUpdate(@Param("id") Long id);
}
