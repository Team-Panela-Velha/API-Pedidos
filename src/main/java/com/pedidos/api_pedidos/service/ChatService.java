package com.pedidos.api_pedidos.service;

import java.util.Arrays;
import java.util.Set;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // Ferramentas do MCP com receita: so ADMIN e MANAGER. O MCP nao sabe quem pergunta; a trava fica aqui (RN10).
    private static final Set<String> REVENUE_MCP_TOOLS = Set.of("resumo_por_periodo");

    private final ChatClient chatClient;
    private final ToolCallbackProvider mcpTools;

    public ChatService(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpTools) {
        this.chatClient = chatClientBuilder.build();
        this.mcpTools = mcpTools;
    }

    static ToolCallback[] visibleMcpTools(ToolCallbackProvider provider, boolean canSeeRevenue) {
        return Arrays.stream(provider.getToolCallbacks())
                .filter(t -> !HIDDEN_MCP_TOOLS.contains(t.getToolDefinition().name()))
                .filter(t -> canSeeRevenue || !REVENUE_MCP_TOOLS.contains(t.getToolDefinition().name()))
                .toArray(ToolCallback[]::new);
    }

    static boolean canSeeRevenue(Authentication auth) {
        if (auth == null) return false;
        for (GrantedAuthority a : auth.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER")) return true;
        }
        return false;
    }

    public String chat(String message) {
        boolean revenue = canSeeRevenue(SecurityContextHolder.getContext().getAuthentication());
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .toolCallbacks(visibleMcpTools(mcpTools, revenue))
                .call()
                .content();
    }
}
