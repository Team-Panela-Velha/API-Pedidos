package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findAllByDeletedAtIsNull();
    Optional<UserEntity> findByIdAndDeletedAtIsNull(Long id);
    Optional<UserEntity> findByEmailAndDeletedAtIsNull(String email);
    long countByDeletedAtIsNull();
    boolean existsByEmail(String email);
}
