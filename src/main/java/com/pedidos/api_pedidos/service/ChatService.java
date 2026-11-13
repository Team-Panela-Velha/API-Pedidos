package com.pedidos.api_pedidos.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private static final String SYSTEM_PROMPT = """
            Você é o assistente operacional do restaurante Pokes & Boas.
            Use as ferramentas MCP disponíveis para responder perguntas sobre cardápio, pedidos,
            comandas e indicadores operacionais. Baseie respostas sobre dados do restaurante
            somente no resultado dessas ferramentas. Se não houver dados suficientes, diga isso
            claramente. Responda em português, de forma objetiva, e não invente informações.
            """;

    private final ChatClient chatClient;
    private final ToolCallbackProvider mcpTools;

    public ChatService(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpTools) {
        this.chatClient = chatClientBuilder.build();
        this.mcpTools = mcpTools;
    }

    public String chat(String message) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .toolCallbacks(mcpTools)
                .call()
                .content();
    }
}
