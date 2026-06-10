package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.ProductExtraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductExtraRepository extends JpaRepository<ProductExtraEntity, Long> {
    List<ProductExtraEntity> findByProductId(Long productId);
}
