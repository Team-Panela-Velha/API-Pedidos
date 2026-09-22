package com.pedidos.api_pedidos.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DashboardResponse(
        Period period,
        Orders orders,
        Revenue revenue,
        Tabs tabs,
        List<RankedItem> topProducts,
        List<RankedItem> topCategories,
        List<DailyPoint> daily,
        DataQuality dataQuality
) {
    public record Period(LocalDate from, LocalDate to, String timezone) {}
    public record StatusCount(String status, long count) {}
    public record Orders(long total, List<StatusCount> byStatus) {}
    public record Revenue(BigDecimal total, BigDecimal averageTicket, BigDecimal addOns, long closedTabs) {}
    public record Tabs(long open, BigDecimal openValue) {}
    public record RankedItem(Long id, String name, long quantity, BigDecimal baseRevenue) {}
    public record DailyPoint(LocalDate date, long orders, BigDecimal revenue) {}
    public record DataQuality(long legacyExtraPricesEstimated, long closedTabsWithoutDate) {}
}
