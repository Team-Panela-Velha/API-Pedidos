package com.pedidos.api_pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pedidos.api_pedidos.domain.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // Função nova 1 — produtos por categoria
    List<ProductEntity> findByCategoryId(Long categoryId);

    // Busca por keyword (nome/descrição/nome da categoria) e/ou categoryId (case-insensitive, match parcial)
    @Query("""
        SELECT p FROM ProductEntity p
        LEFT JOIN p.category c
        WHERE (:keyword IS NULL OR :keyword = '' OR
               LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:categoryId IS NULL OR c.id = :categoryId)
        ORDER BY p.name ASC
        """)
    List<ProductEntity> search(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);
}
