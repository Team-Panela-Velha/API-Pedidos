package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
