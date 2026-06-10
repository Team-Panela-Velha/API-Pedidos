package com.pedidos.api_pedidos.dto.tab;

import java.math.BigDecimal;

public class TabRequest {

    private BigDecimal totalValue;
    private Long tableId;

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public Long getTableId() { return tableId; }
    public void setTableId(Long tableId) { this.tableId = tableId; }
}
