package com.pedidos.api_pedidos.service;

import java.util.Arrays;
import java.util.Set;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "spring.ai.mcp.client", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ChatService {

    private static final String SYSTEM_PROMPT = """
            Você é o assistente operacional do restaurante Pokes & Boas.
            Use as ferramentas MCP disponíveis para responder perguntas sobre cardápio, pedidos,
            comandas e indicadores operacionais. Baseie respostas sobre dados do restaurante
            somente no resultado dessas ferramentas. Se não houver dados suficientes, diga isso
            claramente. Responda em português, de forma objetiva, e não invente informações.
            Para perguntas sobre vendas, faturamento, pedidos do dia ou qualquer período (hoje, ontem,
            semana, mês), use a ferramenta resumo_por_periodo.
            """;

    // Ferramentas do MCP que o chat nao oferece ao modelo. resumo_operacional: soma o historico inteiro (sem data,
    // e a propria descricao diz que "a DDL nao possui datas"), entrega receita a garcom/cozinha e disputa com
    // resumo_por_periodo. Para esconder outra, basta incluir o nome aqui.
    private static final Set<String> HIDDEN_MCP_TOOLS = Set.of("resumo_operacional");

    private final ChatClient chatClient;
    private final ToolCallbackProvider mcpTools;
    private final DashboardTools dashboardTools;

    public ChatService(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpTools,
                       DashboardTools dashboardTools) {
        this.chatClient = chatClientBuilder.build();
        this.mcpTools = mcpTools;
        this.dashboardTools = dashboardTools;
    }

    static ToolCallback[] visibleMcpTools(ToolCallbackProvider provider) {
        return Arrays.stream(provider.getToolCallbacks())
                .filter(t -> !HIDDEN_MCP_TOOLS.contains(t.getToolDefinition().name()))
                .toArray(ToolCallback[]::new);
    }

    public String chat(String message) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .toolCallbacks(visibleMcpTools(mcpTools))
                .tools(dashboardTools)
                .call()
                .content();
    }
}
