package com.pedidos.api_pedidos.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "item_extra")
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

    public ItemExtraEntity() {}

    public ItemExtraEntity(OrderItemEntity orderItem, ExtraEntity extra) {
        this.orderItem = orderItem;
        this.extra = extra;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OrderItemEntity getOrderItem() { return orderItem; }
    public void setOrderItem(OrderItemEntity orderItem) { this.orderItem = orderItem; }

    public ExtraEntity getExtra() { return extra; }
    public void setExtra(ExtraEntity extra) { this.extra = extra; }
}
