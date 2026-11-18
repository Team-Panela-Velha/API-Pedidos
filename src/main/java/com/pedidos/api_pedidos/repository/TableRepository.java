package com.pedidos.api_pedidos.repository;

import com.pedidos.api_pedidos.domain.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface TableRepository extends JpaRepository<TableEntity, Long> {

    List<TableEntity> findAllByDeletedAtIsNull();
    Optional<TableEntity> findByIdAndDeletedAtIsNull(Long id);
    Optional<TableEntity> findByCodeAndDeletedAtIsNull(String code);
    boolean existsByCode(String code);
    boolean existsByNumberAndDeletedAtIsNull(Integer number);
    boolean existsByNumberAndDeletedAtIsNullAndIdNot(Integer number, Long id);
    List<TableEntity> findAllByDeletedAtIsNullAndNumberIsNullOrderByIdAsc();

    @Query("select coalesce(max(t.number), 0) from TableEntity t where t.deletedAt is null")
    int maxActiveNumber();
}
