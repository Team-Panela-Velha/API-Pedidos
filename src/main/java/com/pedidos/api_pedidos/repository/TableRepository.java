package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface TableRepository extends JpaRepository<TableEntity, Long> {

    List<TableEntity> findAllByDeletedAtIsNull();
    Optional<TableEntity> findByIdAndDeletedAtIsNull(Long id);
    Optional<TableEntity> findByCodeAndDeletedAtIsNull(String code);
    boolean existsByCode(String code);
}
