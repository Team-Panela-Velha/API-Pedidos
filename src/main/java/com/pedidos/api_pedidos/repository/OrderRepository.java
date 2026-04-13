package com.pedidos.api_pedidos.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pedidos.api_pedidos.domain.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
}