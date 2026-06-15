package com.pedidos.api_pedidos.dto.payment;

import com.pedidos.api_pedidos.domain.enums.PaymentMethod;

import java.math.BigDecimal;

public class PaymentRequest {

    private Long tabId;
    private PaymentMethod method;
    private BigDecimal amount;

    public Long getTabId() { return tabId; }
    public void setTabId(Long tabId) { this.tabId = tabId; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
