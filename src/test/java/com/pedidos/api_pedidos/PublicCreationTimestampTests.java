package com.pedidos.api_pedidos;

import com.pedidos.api_pedidos.domain.entity.ExtraEntity;
import com.pedidos.api_pedidos.domain.entity.ItemExtraEntity;
import com.pedidos.api_pedidos.domain.entity.OrderEntity;
import com.pedidos.api_pedidos.domain.entity.OrderItemEntity;
import com.pedidos.api_pedidos.domain.entity.ProductEntity;
import com.pedidos.api_pedidos.domain.entity.ProductExtraEntity;
import com.pedidos.api_pedidos.domain.entity.TabEntity;
import com.pedidos.api_pedidos.domain.entity.TableEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PublicCreationTimestampTests {
    @Autowired EntityManager entityManager;

    @Test
    void publicAndAssociativeEntitiesPersistCreationDate() {
        TableEntity table = new TableEntity("T" + UUID.randomUUID().toString().substring(0, 5));
        ProductEntity product = new ProductEntity("Poke", BigDecimal.TEN, null, null, null);
        ExtraEntity extra = new ExtraEntity("Extra", BigDecimal.ONE);
        entityManager.persist(table);
        entityManager.persist(product);
        entityManager.persist(extra);

        TabEntity tab = new TabEntity(BigDecimal.ZERO, table);
        entityManager.persist(tab);
        OrderEntity order = new OrderEntity(tab);
        entityManager.persist(order);
        OrderItemEntity item = new OrderItemEntity(product, order, (short) 1, null, BigDecimal.TEN);
        entityManager.persist(item);
        ProductExtraEntity productExtra = new ProductExtraEntity(product, extra);
        entityManager.persist(productExtra);
        ItemExtraEntity itemExtra = new ItemExtraEntity(item, extra);
        entityManager.persist(itemExtra);

        entityManager.flush();
        entityManager.clear();

        assertThat(entityManager.find(TabEntity.class, tab.getId()).getCreatedAt()).isNotNull();
        assertThat(entityManager.find(OrderEntity.class, order.getId()).getCreatedAt()).isNotNull();
        assertThat(entityManager.find(OrderItemEntity.class, item.getId()).getCreatedAt()).isNotNull();
        assertThat(entityManager.find(ProductExtraEntity.class, productExtra.getId()).getCreatedAt()).isNotNull();
        assertThat(entityManager.find(ItemExtraEntity.class, itemExtra.getId()).getCreatedAt()).isNotNull();
    }
}
