package com.pedidos.api_pedidos.dto.auth;

public class TableAuthResponse {

    private String token;
    private String type = "Bearer";
    private Long tableId;
    private String code;
    private String role = "TABLE";

    public TableAuthResponse() {}

    public TableAuthResponse(String token, Long tableId, String code) {
        this.token = token;
        this.tableId = tableId;
        this.code = code;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getTableId() { return tableId; }
    public void setTableId(Long tableId) { this.tableId = tableId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
