package com.pedidos.api_pedidos.dto.extra;

import java.math.BigDecimal;

public class ExtraRequest {

    private String name;
    private BigDecimal price;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
