package com.pedidos.api_pedidos.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tab_id", nullable = false)
    private TabEntity tab;

    public OrderEntity() {}

    public OrderEntity(TabEntity tab) {
        this.tab = tab;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TabEntity getTab() { return tab; }
    public void setTab(TabEntity tab) { this.tab = tab; }
}
