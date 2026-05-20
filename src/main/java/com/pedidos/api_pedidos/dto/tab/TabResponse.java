package com.pedidos.api_pedidos.dto.tab;

import java.math.BigDecimal;

public class TabResponse {

    private Long id;
    private BigDecimal totalValue;
    private Long tableId;

    public TabResponse(Long id, BigDecimal totalValue, Long tableId) {
        this.id = id;
        this.totalValue = totalValue;
        this.tableId = tableId;
    }

    public Long getId() { return id; }
    public BigDecimal getTotalValue() { return totalValue; }
    public Long getTableId() { return tableId; }
}
