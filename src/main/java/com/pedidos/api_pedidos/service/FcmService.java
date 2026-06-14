package com.pedidos.api_pedidos.service;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.enums.OrderStatus;

@Service
public class FcmService {

    /**
     * Notifica a cozinha sobre um novo pedido.
     * Envia push notification para FCM topic da cozinha com orderId.
     */
    public void notifyKitchen(Long orderId) {
        // TODO: Integrar com Firebase Cloud Messaging para notificar a cozinha
        // Exemplo: enviar push notification para topic 'kitchen' com orderId
        // FirebaseMessaging.getInstance().send(Message.builder()
        //     .setTopic("kitchen")
        //     .putData("orderId", orderId.toString())
        //     .build());
        System.out.println("[FCM] Kitchen notification: New order created with ID " + orderId);
    }

    /**
     * Notifica a mesa sobre a mudança de status do pedido.
     * Envia push notification para FCM token do tablet da mesa.
     */
    public void notifyTable(Long tableId, OrderStatus status) {
        // TODO: Integrar com Firebase Cloud Messaging para notificar a mesa
        // Exemplo: enviar push notification para o token registrado do tablet da mesa
        // Recuperar token do tablet da mesa no banco de dados ou cache
        // FirebaseMessaging.getInstance().send(Message.builder()
        //     .setToken(tabletToken)
        //     .putData("status", status.toString())
        //     .putData("tableId", tableId.toString())
        //     .build());
        System.out.println("[FCM] Table " + tableId + " notification: Order status changed to " + status);
    }

    /**
     * Notifica os garçons sobre uma atividade em uma comanda.
     * Envia push notification para FCM topic dos garçons.
     * Exemplo: novo pedido criado, comanda pronta para cobrança, etc.
     */
    public void notifyWaiter(Long tabId) {
        // TODO: Integrar com Firebase Cloud Messaging para notificar garçons
        // Exemplo: enviar push notification para topic 'waiters' com tabId
        // FirebaseMessaging.getInstance().send(Message.builder()
        //     .setTopic("waiters")
        //     .putData("tabId", tabId.toString())
        //     .putData("action", "order_updated")
        //     .build());
        System.out.println("[FCM] Waiter notification: Tab " + tabId + " activity updated");
    }
}
