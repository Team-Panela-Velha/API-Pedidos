package com.pedidos.api_pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pedidos.api_pedidos.domain.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // Função nova 1 — produtos por categoria
    List<ProductEntity> findByCategoryId(Long categoryId);

    // Função nova 2 — busca por nome do produto + descrição + nome da categoria (case-insensitive, match parcial)
    @Query("""
        SELECT p FROM ProductEntity p
        LEFT JOIN p.category c
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))
        ORDER BY p.name ASC
        """)
    List<ProductEntity> search(@Param("q") String q);
}
