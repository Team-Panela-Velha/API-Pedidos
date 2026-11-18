package com.pedidos.api_pedidos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pedidos.api_pedidos.domain.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findAllByDeletedAtIsNull();
    Optional<ProductEntity> findByIdAndDeletedAtIsNull(Long id);

    // Função nova 1 — produtos por categoria
    List<ProductEntity> findByCategoryIdAndDeletedAtIsNull(Long categoryId);

    // Busca por keyword (nome/descrição/nome da categoria) e/ou categoryId (case-insensitive, match parcial)
    @Query("""
        SELECT p FROM ProductEntity p
        LEFT JOIN p.category c
        WHERE p.deletedAt IS NULL
          AND c.deletedAt IS NULL
          AND (:keyword IS NULL OR :keyword = '' OR
               LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:categoryId IS NULL OR c.id = :categoryId)
          AND (:available IS NULL OR p.available = :available)
        ORDER BY p.name ASC
        """)
    List<ProductEntity> search(@Param("keyword") String keyword,
                               @Param("categoryId") Long categoryId,
                               @Param("available") Boolean available);

    // Mais vendidos de todo o histórico: soma das quantidades, sem itens nem pedidos cancelados
    @Query("""
        SELECT p FROM OrderItemEntity oi
        JOIN oi.product p
        JOIN oi.order o
        WHERE p.deletedAt IS NULL
          AND p.available = true
          AND o.status <> com.pedidos.api_pedidos.domain.enums.OrderStatus.CANCELLED
          AND oi.status <> com.pedidos.api_pedidos.domain.enums.OrderStatus.CANCELLED
        GROUP BY p
        ORDER BY SUM(oi.quantity) DESC, p.name ASC
        """)
    List<ProductEntity> findBestSellers(Pageable pageable);

    // Um produto disponível por categoria ativa: o mais vendido; sem vendas, o primeiro pelo nome
    @Query(nativeQuery = true, value = """
        SELECT DISTINCT ON (c.name, c.id) p.*
        FROM product p
        JOIN category c ON c.id = p.category_id AND c.deleted_at IS NULL
        LEFT JOIN (
            SELECT oi.product_id, SUM(oi.quantity) AS sold
            FROM order_item oi
            JOIN orders o ON o.id = oi.order_id
            WHERE o.status <> 'CANCELLED' AND oi.status <> 'CANCELLED'
            GROUP BY oi.product_id
        ) s ON s.product_id = p.id
        WHERE p.deleted_at IS NULL AND p.available = true
        ORDER BY c.name, c.id, COALESCE(s.sold, 0) DESC, p.name
        """)
    List<ProductEntity> findBestSellerPerCategory();
}
