package com.pedidos.api_pedidos;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.pedidos.api_pedidos.domain.entity.CategoryEntity;
import com.pedidos.api_pedidos.domain.entity.ProductEntity;
import com.pedidos.api_pedidos.repository.CategoryRepository;
import com.pedidos.api_pedidos.repository.ProductRepository;

@SpringBootTest
@Transactional
class ProductSearchTests {
    @Autowired CategoryRepository categories;
    @Autowired ProductRepository products;

    @Test
    void combinesKeywordCategoryAndAvailability() {
        String marker = "Busca" + UUID.randomUUID().toString().replace("-", "");
        CategoryEntity drinks = categories.save(new CategoryEntity(marker, null));
        CategoryEntity others = categories.save(new CategoryEntity("Outra " + marker, null));

        ProductEntity expected = new ProductEntity("Suco", new BigDecimal("8.00"), null, null, drinks);
        ProductEntity unavailable = new ProductEntity("Suco especial", new BigDecimal("9.00"), null, null, drinks);
        unavailable.setAvailable(false);
        ProductEntity wrongCategory = new ProductEntity(marker, new BigDecimal("10.00"), null, null, others);
        products.save(expected);
        products.save(unavailable);
        products.save(wrongCategory);

        assertThat(products.search(marker.toLowerCase(), drinks.getId(), true))
                .extracting(ProductEntity::getId)
                .containsExactly(expected.getId());
    }
}
