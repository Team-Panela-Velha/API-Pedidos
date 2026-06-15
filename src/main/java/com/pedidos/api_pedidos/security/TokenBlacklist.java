package com.pedidos.api_pedidos.security;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Blacklist de tokens JWT em memória.
 * Em produção, substituir por Redis ou banco de dados.
 */
@Component
public class TokenBlacklist {

    private final Set<String> blacklistedTokens = Collections.synchronizedSet(new HashSet<>());

    public void add(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
