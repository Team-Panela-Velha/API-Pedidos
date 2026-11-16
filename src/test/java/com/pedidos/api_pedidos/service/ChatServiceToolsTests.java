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

        ToolCallback[] visible = ChatService.visibleMcpTools(provider);

        assertThat(visible).extracting(t -> t.getToolDefinition().name())
                .containsExactly("listar_produtos", "consultar_comanda");
    }

    @Test
    void semFerramentasEscondidasNadaMuda() {
        ToolCallbackProvider provider = () -> new ToolCallback[] {tool("buscar_produto")};

        assertThat(ChatService.visibleMcpTools(provider)).hasSize(1);
    }
}
