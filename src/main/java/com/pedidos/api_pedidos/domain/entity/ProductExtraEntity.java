package com.pedidos.api_pedidos.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_extra")
public class ProductExtraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "extra_id")
    private ExtraEntity extra;

    public ProductExtraEntity() {}

    public ProductExtraEntity(ProductEntity product, ExtraEntity extra) {
        this.product = product;
        this.extra = extra;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProductEntity getProduct() { return product; }
    public void setProduct(ProductEntity product) { this.product = product; }

    public ExtraEntity getExtra() { return extra; }
    public void setExtra(ExtraEntity extra) { this.extra = extra; }
}
