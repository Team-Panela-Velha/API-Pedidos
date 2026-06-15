package com.pedidos.api_pedidos.domain.entity;

import com.pedidos.api_pedidos.domain.enums.DietaryRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_dietary_restriction")
public class ProductDietaryRestrictionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DietaryRestriction restriction;

    public ProductDietaryRestrictionEntity() {}

    public ProductDietaryRestrictionEntity(ProductEntity product, DietaryRestriction restriction) {
        this.product = product;
        this.restriction = restriction;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProductEntity getProduct() { return product; }
    public void setProduct(ProductEntity product) { this.product = product; }

    public DietaryRestriction getRestriction() { return restriction; }
    public void setRestriction(DietaryRestriction restriction) { this.restriction = restriction; }
}
