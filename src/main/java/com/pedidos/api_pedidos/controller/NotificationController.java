package com.pedidos.api_pedidos.controller;

import org.springframework.web.bind.annotation.*;

import com.pedidos.api_pedidos.dto.notification.NotificationRequest;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @PostMapping
    public void sendNotification(@RequestBody NotificationRequest request) {
        // Ainda não temos integração com serviços de push.
        // TODO: Implementar integração com Firebase Cloud Messaging ou outro serviço
    }
}
