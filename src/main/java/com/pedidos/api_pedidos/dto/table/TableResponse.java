package com.pedidos.api_pedidos.dto.table;

public class TableResponse {

    private Long id;
    private String code;

    public TableResponse(Long id, String code) {
        this.id = id;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }
}