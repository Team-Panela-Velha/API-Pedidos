package com.pedidos.api_pedidos.dto.tab;

import java.math.BigDecimal;
import java.time.Instant;

public class TabResponse {

    private Long id;
    private BigDecimal totalValue;
    private Boolean closed;
    private Long tableId;
    private String tableCode;
    private Instant openedAt;

    public TabResponse(Long id, BigDecimal totalValue, Boolean closed, Long tableId, String tableCode, Instant openedAt) {
        this.id = id;
        this.totalValue = totalValue;
        this.closed = closed;
        this.tableId = tableId;
        this.tableCode = tableCode;
        this.openedAt = openedAt;
    }

    public Long getId() { return id; }
    public BigDecimal getTotalValue() { return totalValue; }
    public Boolean getClosed() { return closed; }
    public Long getTableId() { return tableId; }
    public String getTableCode() { return tableCode; }
    public Instant getOpenedAt() { return openedAt; }
}
