package com.pedidos.api_pedidos.service;

import org.springframework.ai.chat.client.ChatClient;
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
            Para perguntas sobre um período (hoje, ontem, semana, mês), use a ferramenta
            resumo_por_periodo. A ferramenta resumo_operacional cobre todo o histórico, não um dia.
            """;

    private final ChatClient chatClient;
    private final ToolCallbackProvider mcpTools;
    private final DashboardTools dashboardTools;

    public ChatService(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpTools,
                       DashboardTools dashboardTools) {
        this.chatClient = chatClientBuilder.build();
        this.mcpTools = mcpTools;
        this.dashboardTools = dashboardTools;
    }

    public String chat(String message) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .toolCallbacks(mcpTools)
                .tools(dashboardTools)
                .call()
                .content();
    }
}
