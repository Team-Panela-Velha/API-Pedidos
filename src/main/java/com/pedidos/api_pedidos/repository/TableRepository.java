package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TableRepository extends JpaRepository<TableEntity, Long> {

    Optional<TableEntity> findByCode(String code);
}