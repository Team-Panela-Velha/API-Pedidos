package com.pedidos.api_pedidos.controller;

import org.springframework.web.bind.annotation.*;

import com.pedidos.api_pedidos.dto.notification.NotificationRequest;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @PostMapping
    public void sendNotification(@RequestBody NotificationRequest request) {
        // Ainda não temos integração com serviços de push, então vamos logar a notificação
        // TODO: Implementar integração com Firebase Cloud Messaging ou outro serviço
        System.out.println("--- Nova Notificação ---");
        System.out.println("Título: " + request.getTitle());
        System.out.println("Texto: " + request.getBody());
        System.out.println("-------------------------");
    }
}
