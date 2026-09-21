package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.ProductExtraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProductExtraRepository extends JpaRepository<ProductExtraEntity, Long> {
    List<ProductExtraEntity> findByProductId(Long productId);

    @Query("""
        SELECT pe FROM ProductExtraEntity pe
        JOIN pe.product p
        JOIN pe.extra e
        WHERE p.id = :productId
          AND p.deletedAt IS NULL
          AND e.deletedAt IS NULL
        """)
    List<ProductExtraEntity> findActiveByProductId(@Param("productId") Long productId);

    @Query("""
        SELECT pe FROM ProductExtraEntity pe
        JOIN pe.product p
        JOIN pe.extra e
        WHERE p.deletedAt IS NULL
          AND e.deletedAt IS NULL
        """)
    List<ProductExtraEntity> findAllActive();
}
