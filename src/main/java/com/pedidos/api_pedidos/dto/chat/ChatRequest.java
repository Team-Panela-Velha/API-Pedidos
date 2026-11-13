package com.pedidos.api_pedidos.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank(message = "A mensagem é obrigatória")
        @Size(max = 4000, message = "A mensagem deve ter no máximo 4000 caracteres")
        String message) {
}
