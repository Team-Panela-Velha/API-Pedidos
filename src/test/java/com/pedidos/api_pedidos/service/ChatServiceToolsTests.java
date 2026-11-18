package com.pedidos.api_pedidos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;

class ChatServiceToolsTests {

    private static ToolCallback tool(String name) {
        ToolCallback callback = mock(ToolCallback.class);
        when(callback.getToolDefinition())
                .thenReturn(ToolDefinition.builder().name(name).description("d").inputSchema("{}").build());
        return callback;
    }

    @Test
    void resumoOperacionalDoMcpNaoVaiParaOModelo() {
        ToolCallbackProvider provider = () -> new ToolCallback[] {
                tool("listar_produtos"), tool("resumo_operacional"), tool("consultar_comanda")};

        ToolCallback[] visible = ChatService.visibleMcpTools(provider, true);

        assertThat(visible).extracting(t -> t.getToolDefinition().name())
                .containsExactly("listar_produtos", "consultar_comanda");
    }

    @Test
    void semFerramentasEscondidasNadaMuda() {
        ToolCallbackProvider provider = () -> new ToolCallback[] {tool("buscar_produto")};

        assertThat(ChatService.visibleMcpTools(provider, true)).hasSize(1);
    }

    @Test
    void resumoPorPeriodoSoParaQuemPodeVerReceita() {
        ToolCallbackProvider provider = () -> new ToolCallback[] {tool("listar_produtos"), tool("resumo_por_periodo")};

        assertThat(ChatService.visibleMcpTools(provider, true)).extracting(t -> t.getToolDefinition().name())
                .containsExactly("listar_produtos", "resumo_por_periodo");
        assertThat(ChatService.visibleMcpTools(provider, false)).extracting(t -> t.getToolDefinition().name())
                .containsExactly("listar_produtos");
    }

    @Test
    void soAdminEManagerVeemReceita() {
        assertThat(ChatService.canSeeRevenue(auth("ROLE_ADMIN"))).isTrue();
        assertThat(ChatService.canSeeRevenue(auth("ROLE_MANAGER"))).isTrue();
        assertThat(ChatService.canSeeRevenue(auth("ROLE_WAITER"))).isFalse();
        assertThat(ChatService.canSeeRevenue(auth("ROLE_KITCHEN"))).isFalse();
        assertThat(ChatService.canSeeRevenue(null)).isFalse();
    }

    private static org.springframework.security.core.Authentication auth(String role) {
        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("u", "t",
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role)));
    }
}