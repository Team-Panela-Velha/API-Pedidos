package com.pedidos.api_pedidos.dto.payment;

import com.pedidos.api_pedidos.domain.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentResponse {

    private Long id;
    private Long tabId;
    private PaymentMethod method;
    private BigDecimal amount;
    private Instant paidAt;

    public PaymentResponse(Long id, Long tabId, PaymentMethod method, BigDecimal amount, Instant paidAt) {
        this.id = id;
        this.tabId = tabId;
        this.method = method;
        this.amount = amount;
        this.paidAt = paidAt;
    }

    public Long getId() { return id; }
    public Long getTabId() { return tabId; }
    public PaymentMethod getMethod() { return method; }
    public BigDecimal getAmount() { return amount; }
    public Instant getPaidAt() { return paidAt; }
}
