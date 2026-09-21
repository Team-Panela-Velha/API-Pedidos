package com.pedidos.api_pedidos;

import com.pedidos.api_pedidos.domain.entity.CategoryEntity;
import com.pedidos.api_pedidos.domain.entity.ExtraEntity;
import com.pedidos.api_pedidos.domain.entity.ProductEntity;
import com.pedidos.api_pedidos.domain.entity.TableEntity;
import com.pedidos.api_pedidos.domain.entity.UserEntity;
import com.pedidos.api_pedidos.repository.CategoryRepository;
import com.pedidos.api_pedidos.service.AuditService;
import com.pedidos.api_pedidos.service.CategoryService;
import org.junit.jupiter.api.Test;
import jakarta.persistence.Column;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SoftDeleteTests {

    @Test
    void auditColumnsAreDeclaredDirectlyOnEachAuditedEntity() throws Exception {
        String[] fields = {"createdAt", "createdBy", "updatedAt", "updatedBy", "deletedAt", "deletedBy"};
        String[] columns = {"created_at", "created_by", "updated_at", "updated_by", "deleted_at", "deleted_by"};
        Class<?>[] entities = {CategoryEntity.class, ProductEntity.class, ExtraEntity.class,
                TableEntity.class, UserEntity.class};

        for (Class<?> entity : entities) {
            for (int index = 0; index < fields.length; index++) {
                assertThat(entity.getDeclaredField(fields[index]).getAnnotation(Column.class).name())
                        .as(entity.getSimpleName() + "." + fields[index])
                        .isEqualTo(columns[index]);
            }
        }
    }

    @Test
    void categoryDeletionRecordsActorAndKeepsDatabaseRow() {
        CategoryRepository categories = mock(CategoryRepository.class);
        AuditService audit = mock(AuditService.class);
        CategoryEntity category = new CategoryEntity("Pokes", null);
        category.setId(10L);

        when(categories.findByIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(category));
        when(categories.existsProductByCategoryId(10L)).thenReturn(false);
        when(audit.currentUserId()).thenReturn(7L);

        new CategoryService(categories, audit).delete(10L);

        assertThat(category.getDeletedAt()).isNotNull();
        assertThat(category.getDeletedBy()).isEqualTo(7L);
        verify(categories).save(category);
        verify(categories, never()).deleteById(10L);
    }
}
