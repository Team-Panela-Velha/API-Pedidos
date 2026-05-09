package com.pedidos.api_pedidos.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "extra")
public class ExtraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "extra_id", updatable = false, nullable = false)
    private UUID extraId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public ExtraEntity() {}

    public ExtraEntity(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public UUID getExtraId() {
        return extraId;
    }

    public void setExtraId(UUID extraId) {
        this.extraId = extraId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
