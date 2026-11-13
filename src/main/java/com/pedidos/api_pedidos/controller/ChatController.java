package com.pedidos.api_pedidos.controller;

import com.pedidos.api_pedidos.dto.chat.ChatRequest;
import com.pedidos.api_pedidos.dto.chat.ChatResponse;
import com.pedidos.api_pedidos.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return new ChatResponse(chatService.chat(request.message()));
    }
}
