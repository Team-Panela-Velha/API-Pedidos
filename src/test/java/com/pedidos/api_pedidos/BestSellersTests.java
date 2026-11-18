package com.pedidos.api_pedidos;

import static org.assertj.core.api.Assertions.assertThat;

import com.pedidos.api_pedidos.dto.product.ProductResponse;
import com.pedidos.api_pedidos.service.ProductService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BestSellersTests {
    @Autowired JdbcTemplate jdbc;
    @Autowired ProductService products;

    private long insert(String sql, Object... args) {
        return jdbc.queryForObject(sql + " returning id", Long.class, args);
    }

    private long product(String name, boolean available) {
        return insert("insert into product(name, price, available) values (?, 10.00, ?)", name, available);
    }

    private void sell(long orderId, long productId, int quantity, String status) {
        insert("""
                insert into order_item(product_id, order_id, quantity, unit_price_snapshot, status, created_at)
                values (?, ?, ?, 10.00, ?, now())
                """, productId, orderId, quantity, status);
    }

    @Test
    void ranksAvailableProductsBySoldQuantityIgnoringCancelled() {
        String marker = UUID.randomUUID().toString();
        long top = product("Top " + marker, true);
        long second = product("Second " + marker, true);
        long cancelledOnly = product("Cancelled " + marker, true);
        long unavailable = product("Off " + marker, false);
        long tableId = insert("insert into restaurant_table(code) values (?)", marker.substring(0, 8));
        long tabId = insert("""
                insert into tab(total_value, status, opened_at, created_at, closed, table_id)
                values (0, 'OPEN', now(), now(), false, ?)
                """, tableId);
        long orderId = insert("insert into orders(tab_id, status, created_at, status_updated_at) values (?, 'RECEIVED', now(), now())", tabId);
        long cancelledOrder = insert("insert into orders(tab_id, status, created_at, status_updated_at) values (?, 'CANCELLED', now(), now())", tabId);

        sell(orderId, top, 30000, "RECEIVED");
        sell(orderId, second, 20000, "RECEIVED");
        sell(orderId, cancelledOnly, 32000, "CANCELLED");
        sell(cancelledOrder, cancelledOnly, 32000, "RECEIVED");
        sell(orderId, unavailable, 32000, "RECEIVED");

        assertThat(products.getBestSellers(2))
                .extracting(ProductResponse::getId)
                .containsExactly(top, second);
    }

    @Test
    void picksTheBestSellerOfEachCategory() {
        String marker = UUID.randomUUID().toString();
        long drinks = insert("insert into category(name) values (?)", "Drinks " + marker);
        long desserts = insert("insert into category(name) values (?)", "Desserts " + marker);
        long juice = insert("insert into product(name, price, available, category_id) values (?, 9, true, ?)", "Juice " + marker, drinks);
        long soda = insert("insert into product(name, price, available, category_id) values (?, 7, true, ?)", "Soda " + marker, drinks);
        long offDrink = insert("insert into product(name, price, available, category_id) values (?, 7, false, ?)", "Off " + marker, drinks);
        long mochi = insert("insert into product(name, price, available, category_id) values (?, 12, true, ?)", "Mochi " + marker, desserts);
        long tableId = insert("insert into restaurant_table(code) values (?)", marker.substring(0, 8));
        long tabId = insert("""
                insert into tab(total_value, status, opened_at, created_at, closed, table_id)
                values (0, 'OPEN', now(), now(), false, ?)
                """, tableId);
        long orderId = insert("insert into orders(tab_id, status, created_at, status_updated_at) values (?, 'RECEIVED', now(), now())", tabId);

        sell(orderId, soda, 3, "RECEIVED");
        sell(orderId, juice, 2, "RECEIVED");
        sell(orderId, juice, 5, "CANCELLED");
        sell(orderId, offDrink, 9, "RECEIVED");

        // drinks: soda sold the most; desserts: no sales, still gets one product
        assertThat(products.getBestSellerPerCategory())
                .extracting(ProductResponse::getId)
                .contains(soda, mochi)
                .doesNotContain(juice, offDrink);
    }
}
