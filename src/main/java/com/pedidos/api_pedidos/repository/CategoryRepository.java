package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findAllByDeletedAtIsNull();
    Optional<CategoryEntity> findByIdAndDeletedAtIsNull(Long id);
    boolean existsByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT COUNT(p) > 0 FROM ProductEntity p WHERE p.category.id = :categoryId AND p.deletedAt IS NULL")
    boolean existsProductByCategoryId(@Param("categoryId") Long categoryId);
}
