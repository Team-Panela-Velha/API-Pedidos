package com.pedidos.api_pedidos.service;

import com.pedidos.api_pedidos.domain.enums.OrderStatus;
import com.pedidos.api_pedidos.dto.dashboard.DashboardResponse;
import com.pedidos.api_pedidos.dto.dashboard.DashboardResponse.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DashboardService {
    public static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private final JdbcTemplate jdbc;
    private final Clock clock;

    @Autowired
    public DashboardService(JdbcTemplate jdbc) {
        this(jdbc, Clock.system(ZONE));
    }

    DashboardService(JdbcTemplate jdbc, Clock clock) {
        this.jdbc = jdbc;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public DashboardResponse get(LocalDate requestedFrom, LocalDate requestedTo) {
        LocalDate today = LocalDate.now(clock.withZone(ZONE));
        LocalDate to = requestedTo == null ? today : requestedTo;
        LocalDate from = requestedFrom == null ? (requestedTo == null ? today : to) : requestedFrom;
        if (from.isAfter(to) || from.plusDays(366).isBefore(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O período deve ter de 1 a 367 dias");
        }
        Instant startInstant = from.atStartOfDay(ZONE).toInstant();
        Instant endInstant = to.plusDays(1).atStartOfDay(ZONE).toInstant();
        Timestamp start = Timestamp.from(startInstant);
        Timestamp end = Timestamp.from(endInstant);

        Map<OrderStatus, Long> statusCounts = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) statusCounts.put(status, 0L);
        jdbc.query("select status, count(*) as amount from orders where created_at >= ? and created_at < ? group by status",
                rs -> { statusCounts.put(OrderStatus.valueOf(rs.getString("status")), rs.getLong("amount")); }, start, end);
        List<StatusCount> byStatus = statusCounts.entrySet().stream()
                .map(entry -> new StatusCount(entry.getKey().name(), entry.getValue())).toList();
        long totalOrders = statusCounts.values().stream().mapToLong(Long::longValue).sum();

        Revenue revenue = jdbc.queryForObject("""
                select coalesce(sum(total_value), 0) as total, count(*) as closed_tabs
                from tab where closed = true and closed_at >= ? and closed_at < ?
                """, (rs, row) -> {
            BigDecimal total = rs.getBigDecimal("total");
            long count = rs.getLong("closed_tabs");
            return new Revenue(total, count == 0 ? BigDecimal.ZERO :
                    total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP), BigDecimal.ZERO, count);
        }, start, end);

        Tabs tabs = jdbc.queryForObject("""
                select count(*) as open_tabs, coalesce(sum(total_value), 0) as open_value
                from tab where closed = false
                """, (rs, row) -> new Tabs(rs.getLong("open_tabs"), rs.getBigDecimal("open_value")));

        List<RankedItem> products = jdbc.query("""
                select p.id, p.name, sum(oi.quantity) as quantity,
                       sum(oi.unit_price_snapshot * oi.quantity) as base_revenue
                from order_item oi
                join orders o on o.id = oi.order_id
                join product p on p.id = oi.product_id
                where o.created_at >= ? and o.created_at < ?
                  and o.status <> 'CANCELLED' and oi.status <> 'CANCELLED'
                group by p.id, p.name
                order by quantity desc, p.name asc
                limit 5
                """, (rs, row) -> new RankedItem(rs.getLong("id"), rs.getString("name"),
                rs.getLong("quantity"), rs.getBigDecimal("base_revenue")), start, end);

        List<RankedItem> categories = jdbc.query("""
                select c.id, c.name, sum(oi.quantity) as quantity,
                       sum(oi.unit_price_snapshot * oi.quantity) as base_revenue
                from order_item oi
                join orders o on o.id = oi.order_id
                join product p on p.id = oi.product_id
                join category c on c.id = p.category_id
                where o.created_at >= ? and o.created_at < ?
                  and o.status <> 'CANCELLED' and oi.status <> 'CANCELLED'
                group by c.id, c.name
                order by quantity desc, c.name asc
                limit 5
                """, (rs, row) -> new RankedItem(rs.getLong("id"), rs.getString("name"),
                rs.getLong("quantity"), rs.getBigDecimal("base_revenue")), start, end);

        BigDecimal addOns = jdbc.queryForObject("""
                select coalesce(sum(coalesce(ie.unit_price_snapshot, e.price) * oi.quantity), 0)
                from item_extra ie
                join order_item oi on oi.id = ie.order_item_id
                join orders o on o.id = oi.order_id
                join tab t on t.id = o.tab_id
                join extra e on e.id = ie.extra_id
                where t.closed = true and t.closed_at >= ? and t.closed_at < ?
                  and o.status <> 'CANCELLED' and oi.status <> 'CANCELLED'
                """, BigDecimal.class, start, end);
        long legacyPrices = jdbc.queryForObject("""
                select count(*) from item_extra ie
                join order_item oi on oi.id = ie.order_item_id
                join orders o on o.id = oi.order_id
                join tab t on t.id = o.tab_id
                where t.closed = true and t.closed_at >= ? and t.closed_at < ?
                  and ie.unit_price_snapshot is null
                  and o.status <> 'CANCELLED' and oi.status <> 'CANCELLED'
                """, Long.class, start, end);
        long undatedClosedTabs = jdbc.queryForObject(
                "select count(*) from tab where closed = true and closed_at is null", Long.class);
        revenue = new Revenue(revenue.total(), revenue.averageTicket(), addOns, revenue.closedTabs());

        Map<LocalDate, Long> dailyOrders = new HashMap<>();
        jdbc.query("""
                select date(timezone(?, created_at)) as day, count(*) as amount
                from orders where created_at >= ? and created_at < ?
                group by day
                """, rs -> { dailyOrders.put(rs.getDate("day").toLocalDate(), rs.getLong("amount")); },
                ZONE.getId(), start, end);
        Map<LocalDate, BigDecimal> dailyRevenue = new HashMap<>();
        jdbc.query("""
                select date(timezone(?, closed_at)) as day, sum(total_value) as amount
                from tab where closed = true and closed_at >= ? and closed_at < ?
                group by day
                """, rs -> { dailyRevenue.put(rs.getDate("day").toLocalDate(), rs.getBigDecimal("amount")); },
                ZONE.getId(), start, end);
        List<DailyPoint> daily = new ArrayList<>();
        for (LocalDate day = from; !day.isAfter(to); day = day.plusDays(1)) {
            daily.add(new DailyPoint(day, dailyOrders.getOrDefault(day, 0L),
                    dailyRevenue.getOrDefault(day, BigDecimal.ZERO)));
        }
        return new DashboardResponse(new Period(from, to, ZONE.getId()),
                new Orders(totalOrders, byStatus), revenue, tabs, products, categories,
                daily, new DataQuality(legacyPrices, undatedClosedTabs));
    }
}
