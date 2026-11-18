package com.pedidos.api_pedidos.dto.table;

public class TableRequest {

    private String code;
    // Opcional: sem numero, a API usa o proximo livre.
    private Integer number;

    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
