package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("SELECT COUNT(p) > 0 FROM ProductEntity p WHERE p.category.id = :categoryId")
    boolean existsProductByCategoryId(@Param("categoryId") Long categoryId);
}
