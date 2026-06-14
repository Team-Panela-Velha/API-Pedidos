package com.pedidos.api_pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pedidos.api_pedidos.domain.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
