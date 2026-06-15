package com.pedidos.api_pedidos.domain.entity;

import com.pedidos.api_pedidos.domain.enums.PaymentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tab_id", nullable = false)
    private TabEntity tab;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "paid_at", nullable = false)
    private Instant paidAt;

    @ManyToOne
    @JoinColumn(name = "registered_by_id")
    private StaffUserEntity registeredBy;

    public PaymentEntity() {}

    public PaymentEntity(TabEntity tab, PaymentMethod paymentMethod, BigDecimal amount,
                         Instant paidAt, StaffUserEntity registeredBy) {
        this.tab = tab;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.paidAt = paidAt;
        this.registeredBy = registeredBy;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TabEntity getTab() { return tab; }
    public void setTab(TabEntity tab) { this.tab = tab; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Instant getPaidAt() { return paidAt; }
    public void setPaidAt(Instant paidAt) { this.paidAt = paidAt; }

    public StaffUserEntity getRegisteredBy() { return registeredBy; }
    public void setRegisteredBy(StaffUserEntity registeredBy) { this.registeredBy = registeredBy; }
}
