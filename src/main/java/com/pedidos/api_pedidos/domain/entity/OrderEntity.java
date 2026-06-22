package com.pedidos.api_pedidos.domain.entity;

import com.pedidos.api_pedidos.domain.enums.OrderStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tab_id", nullable = false)
    private TabEntity tab;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.RECEIVED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "status_updated_at", nullable = false)
    private Instant statusUpdatedAt;

    public OrderEntity() {}

    public OrderEntity(TabEntity tab) {
        this.tab = tab;
        this.status = OrderStatus.RECEIVED;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TabEntity getTab() { return tab; }
    public void setTab(TabEntity tab) { this.tab = tab; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getStatusUpdatedAt() { return statusUpdatedAt; }
    public void setStatusUpdatedAt(Instant statusUpdatedAt) { this.statusUpdatedAt = statusUpdatedAt; }
}
