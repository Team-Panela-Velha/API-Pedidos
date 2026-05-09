package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.ExtraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExtraRepository extends JpaRepository<ExtraEntity, UUID> {
}
