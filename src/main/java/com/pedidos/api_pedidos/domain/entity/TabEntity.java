package com.pedidos.api_pedidos.domain.entity;

import com.pedidos.api_pedidos.domain.enums.TabStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "tab")
public class TabEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_value", nullable = false, precision = 8, scale = 2)
    private BigDecimal totalValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TabStatus status = TabStatus.OPEN;

    @CreationTimestamp
    @Column(name = "opened_at", nullable = false, updatable = false)
    private Instant openedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @ManyToOne
    @JoinColumn(name = "closed_by_id")
    private StaffUserEntity closedBy;

    @Column(nullable = false)
    private Boolean closed = false;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableEntity table;

    public TabEntity() {}

    public TabEntity(BigDecimal totalValue, TableEntity table) {
        this.totalValue = totalValue;
        this.table = table;
        this.closed = false;
        this.status = TabStatus.OPEN;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public TabStatus getStatus() { return status; }
    public void setStatus(TabStatus status) { this.status = status; }

    public Instant getOpenedAt() { return openedAt; }
    public void setOpenedAt(Instant openedAt) { this.openedAt = openedAt; }

    public Instant getClosedAt() { return closedAt; }
    public void setClosedAt(Instant closedAt) { this.closedAt = closedAt; }

    public StaffUserEntity getClosedBy() { return closedBy; }
    public void setClosedBy(StaffUserEntity closedBy) { this.closedBy = closedBy; }

    public Boolean getClosed() { return closed; }
    public void setClosed(Boolean closed) { this.closed = closed; }

    public TableEntity getTable() { return table; }
    public void setTable(TableEntity table) { this.table = table; }
}
