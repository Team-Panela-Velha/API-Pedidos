package com.pedidos.api_pedidos.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class TableAuthRequest {

    @NotBlank(message = "Código é obrigatório")
    private String code;

    public TableAuthRequest() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
