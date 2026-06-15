package com.pedidos.api_pedidos.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "\"table\"")
public class TableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "table_id", updatable = false, nullable = false)
    private UUID tableId;

    @Column(nullable = false)
    private String code;


    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    private List<TabModel> tabs;
}