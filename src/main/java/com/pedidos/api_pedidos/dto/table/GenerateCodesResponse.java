package com.pedidos.api_pedidos.dto.table;

public class GenerateCodesResponse {

    private String loginCode;
    private String logoutCode;

    public GenerateCodesResponse(String loginCode, String logoutCode) {
        this.loginCode = loginCode;
        this.logoutCode = logoutCode;
    }

    public String getLoginCode() { return loginCode; }
    public String getLogoutCode() { return logoutCode; }
}
