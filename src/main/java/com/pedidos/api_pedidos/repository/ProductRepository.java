package com.pedidos.api_pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pedidos.api_pedidos.domain.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
