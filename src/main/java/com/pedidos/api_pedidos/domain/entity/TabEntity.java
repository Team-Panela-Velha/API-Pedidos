package com.pedidos.api_pedidos.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tab")
public class TabEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_value", nullable = false)
    private BigDecimal totalValue;

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
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public Boolean getClosed() { return closed; }
    public void setClosed(Boolean closed) { this.closed = closed; }

    public TableEntity getTable() { return table; }
    public void setTable(TableEntity table) { this.table = table; }
}
