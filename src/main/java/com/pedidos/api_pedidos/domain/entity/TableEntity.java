package com.pedidos.api_pedidos.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_table")
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    protected TableEntity() {
    }

    public TableEntity(String code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }
}