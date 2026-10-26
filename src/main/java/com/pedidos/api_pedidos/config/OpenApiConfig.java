package com.pedidos.api_pedidos.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecuritySchemes({
        @SecurityScheme(name = OpenApiConfig.BEARER_AUTH, type = SecuritySchemeType.HTTP,
                scheme = "bearer", bearerFormat = "JWT"),
        @SecurityScheme(name = OpenApiConfig.BASIC_AUTH, type = SecuritySchemeType.HTTP,
                scheme = "basic", description = "Use o e-mail e a senha de um funcionário")
})
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";
    public static final String BASIC_AUTH = "basicAuth";

    @Bean
    OpenApiCustomizer authenticationResponses() {
        return openApi -> {
            openApi.setSecurity(List.of(
                    new SecurityRequirement().addList(BEARER_AUTH),
                    new SecurityRequirement().addList(BASIC_AUTH)));
            openApi.getPaths().forEach((path, pathItem) -> {
                if (isPublicPath(path)) {
                    return;
                }
                pathItem.readOperations().forEach(operation -> {
                    operation.getResponses().addApiResponse("401",
                            new ApiResponse().description("Credenciais ausentes, inválidas ou expiradas"));
                    operation.getResponses().addApiResponse("403",
                            new ApiResponse().description("Usuário autenticado sem permissão para a operação"));
                });
            });
        };
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/field/")
                || path.equals("/auth/login")
                || path.equals("/auth/register");
    }
}
