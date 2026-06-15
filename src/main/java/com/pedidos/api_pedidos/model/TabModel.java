package com.pedidos.api_pedidos.model;

import jakarta.persistence.*;
import java.util.UUID;


@Entity
@Table(name = "tab")
public class TabModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tab_id", updatable = false, nullable = false)
    private UUID tabId;

    @Column(name = "total_value", nullable = false)
    private Double totalValue;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableModel table;


}