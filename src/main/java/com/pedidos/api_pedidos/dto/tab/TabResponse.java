package com.pedidos.api_pedidos.dto.tab;

import java.math.BigDecimal;

public class TabResponse {

    private Long id;
    private BigDecimal totalValue;
    private Boolean closed;
    private Long tableId;
    private String tableCode;

    public TabResponse(Long id, BigDecimal totalValue, Boolean closed, Long tableId, String tableCode) {
        this.id = id;
        this.totalValue = totalValue;
        this.closed = closed;
        this.tableId = tableId;
        this.tableCode = tableCode;
    }

    public Long getId() { return id; }
    public BigDecimal getTotalValue() { return totalValue; }
    public Boolean getClosed() { return closed; }
    public Long getTableId() { return tableId; }
    public String getTableCode() { return tableCode; }
}
