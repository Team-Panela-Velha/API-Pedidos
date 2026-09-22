package com.pedidos.api_pedidos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pedidos.api_pedidos.dto.dashboard.DashboardResponse;
import com.pedidos.api_pedidos.service.DashboardService;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@Transactional
class DashboardTests {
    @Autowired JdbcTemplate jdbc;
    @Autowired DashboardService dashboard;

    private long insert(String sql, Object... args) {
        return jdbc.queryForObject(sql + " returning id", Long.class, args);
    }

    @Test
    void aggregatesOrdersClosedTabsAndExtraSnapshotsWithinRestaurantDays() {
        long categoryId = insert("insert into category(name) values (?)", "Bowls");
        long productId = insert("insert into product(name, price, available, category_id) values (?, ?, true, ?)",
                "Poke", new BigDecimal("10.00"), categoryId);
        long extraId = insert("insert into extra(name, price) values (?, ?)", "Molho", new BigDecimal("9.00"));
        long tableId = insert("insert into restaurant_table(code) values (?)", "DASH-TEST");
        long tabId = insert("""
                insert into tab(total_value, status, opened_at, created_at, closed_at, closed, table_id)
                values (?, 'CLOSED', ?, ?, ?, true, ?)
                """, new BigDecimal("30.00"), Timestamp.from(Instant.parse("2040-01-01T23:00:00Z")),
                Timestamp.from(Instant.parse("2040-01-01T23:00:00Z")),
                Timestamp.from(Instant.parse("2040-01-02T04:00:00Z")), tableId);
        long orderId = insert("""
                insert into orders(tab_id, status, created_at, status_updated_at)
                values (?, 'RECEIVED', ?, ?)
                """, tabId, Timestamp.from(Instant.parse("2040-01-02T01:30:00Z")),
                Timestamp.from(Instant.parse("2040-01-02T01:30:00Z")));
        long itemId = insert("""
                insert into order_item(product_id, order_id, quantity, unit_price_snapshot, status, created_at)
                values (?, ?, 2, ?, 'RECEIVED', ?)
                """, productId, orderId, new BigDecimal("10.00"),
                Timestamp.from(Instant.parse("2040-01-02T01:30:00Z")));
        insert("insert into item_extra(order_item_id, extra_id, unit_price_snapshot) values (?, ?, ?)",
                itemId, extraId, new BigDecimal("3.00"));
        long legacyExtraId = insert("insert into extra(name, price) values (?, ?)", "Gergelim", new BigDecimal("2.00"));
        insert("insert into item_extra(order_item_id, extra_id) values (?, ?)", itemId, legacyExtraId);

        DashboardResponse jan1 = dashboard.get(LocalDate.parse("2040-01-01"), LocalDate.parse("2040-01-01"));
        assertThat(jan1.orders().total()).isEqualTo(1);
        assertThat(jan1.revenue().total()).isEqualByComparingTo("0");
        assertThat(jan1.topProducts()).hasSize(1);
        assertThat(jan1.topProducts().getFirst().quantity()).isEqualTo(2);
        assertThat(jan1.topProducts().getFirst().baseRevenue()).isEqualByComparingTo("20.00");
        assertThat(jan1.topCategories().getFirst().name()).isEqualTo("Bowls");
        assertThat(jan1.daily().getFirst().orders()).isEqualTo(1);

        DashboardResponse jan2 = dashboard.get(LocalDate.parse("2040-01-02"), LocalDate.parse("2040-01-02"));
        assertThat(jan2.orders().total()).isZero();
        assertThat(jan2.revenue().total()).isEqualByComparingTo("30.00");
        assertThat(jan2.revenue().averageTicket()).isEqualByComparingTo("30.00");
        assertThat(jan2.revenue().addOns()).isEqualByComparingTo("10.00");
        assertThat(jan2.dataQuality().legacyExtraPricesEstimated()).isEqualTo(1);
        assertThat(jan2.daily().getFirst().revenue()).isEqualByComparingTo("30.00");
        assertThat(jan2.period().timezone()).isEqualTo("America/Sao_Paulo");
    }

    @Test
    void rejectsInvertedOrExcessivePeriods() {
        ResponseStatusException inverted = assertThrows(ResponseStatusException.class,
                () -> dashboard.get(LocalDate.parse("2040-02-02"), LocalDate.parse("2040-02-01")));
        assertThat(inverted.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThrows(ResponseStatusException.class,
                () -> dashboard.get(LocalDate.parse("2040-01-01"), LocalDate.parse("2041-02-01")));
    }
}
