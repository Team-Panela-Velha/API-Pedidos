package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
