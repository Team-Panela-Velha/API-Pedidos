package com.pedidos.api_pedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pedidos.api_pedidos.domain.entity.ProductDietaryRestrictionEntity;
import com.pedidos.api_pedidos.domain.enums.DietaryRestriction;

public interface ProductDietaryRestrictionRepository extends JpaRepository<ProductDietaryRestrictionEntity, Long> {
    List<ProductDietaryRestrictionEntity> findByRestrictionIn(List<DietaryRestriction> restrictions);
}
