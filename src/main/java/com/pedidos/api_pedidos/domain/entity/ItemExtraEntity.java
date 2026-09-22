package com.pedidos.api_pedidos.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.math.BigDecimal;

@Entity
@Table(name = "item_extra", indexes = @Index(name = "idx_item_extra_order_item", columnList = "order_item_id"))
public class ItemExtraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItemEntity orderItem;

    @ManyToOne
    @JoinColumn(name = "extra_id", nullable = false)
    private ExtraEntity extra;

    @Column(name = "unit_price_snapshot", precision = 8, scale = 2)
    private BigDecimal unitPriceSnapshot;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public ItemExtraEntity() {}

    public ItemExtraEntity(OrderItemEntity orderItem, ExtraEntity extra) {
        this.orderItem = orderItem;
        this.extra = extra;
        this.unitPriceSnapshot = extra.getPrice();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OrderItemEntity getOrderItem() { return orderItem; }
    public void setOrderItem(OrderItemEntity orderItem) { this.orderItem = orderItem; }

    public ExtraEntity getExtra() { return extra; }
    public void setExtra(ExtraEntity extra) { this.extra = extra; }
    public BigDecimal getUnitPriceSnapshot() { return unitPriceSnapshot; }

    public Instant getCreatedAt() { return createdAt; }
}
