package com.pedidos.api_pedidos.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Column(nullable = false)
    private Short quantity;

    @Column(columnDefinition = "text")
    private String observation;

    @Column(name = "unit_price_snapshot", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPriceSnapshot;

    public OrderItemEntity() {}

    public OrderItemEntity(ProductEntity product, OrderEntity order, Short quantity, String observation, BigDecimal unitPriceSnapshot) {
        this.product = product;
        this.order = order;
        this.quantity = quantity;
        this.observation = observation;
        this.unitPriceSnapshot = unitPriceSnapshot;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProductEntity getProduct() { return product; }
    public void setProduct(ProductEntity product) { this.product = product; }

    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }

    public Short getQuantity() { return quantity; }
    public void setQuantity(Short quantity) { this.quantity = quantity; }

    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }

    public BigDecimal getUnitPriceSnapshot() { return unitPriceSnapshot; }
    public void setUnitPriceSnapshot(BigDecimal unitPriceSnapshot) { this.unitPriceSnapshot = unitPriceSnapshot; }
}
