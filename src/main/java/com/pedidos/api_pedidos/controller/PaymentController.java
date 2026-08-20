package com.pedidos.api_pedidos.controller;

import com.pedidos.api_pedidos.dto.payment.PaymentRequest;
import com.pedidos.api_pedidos.dto.payment.PaymentResponse;
import com.pedidos.api_pedidos.security.JwtUtil;
import com.pedidos.api_pedidos.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;
    private final JwtUtil jwtUtil;

    public PaymentController(PaymentService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody PaymentRequest request,
                                                               Authentication authentication) {
        Long staffUserId = extractStaffUserId(authentication);
        return ResponseEntity.ok(service.create(request, staffUserId));
    }

    @GetMapping("/tab/{tabId}")
    public ResponseEntity<List<PaymentResponse>> getByTab(@PathVariable Long tabId) {
        return ResponseEntity.ok(service.getByTab(tabId));
    }

    /** Extrai o id do staff (claim "id") do token JWT presente nas credenciais; null se for token de mesa. */
    private Long extractStaffUserId(Authentication authentication) {
        if (authentication == null || authentication.getCredentials() == null) {
            return null;
        }
        try {
            return jwtUtil.extractId(authentication.getCredentials().toString());
        } catch (Exception e) {
            return null;
        }
    }
}
