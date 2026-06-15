package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.StaffUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffUserRepository extends JpaRepository<StaffUserEntity, Long> {
    Optional<StaffUserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
