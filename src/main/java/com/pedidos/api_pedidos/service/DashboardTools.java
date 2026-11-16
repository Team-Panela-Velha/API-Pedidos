package com.pedidos.api_pedidos.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/** Ferramenta do chatbot: vendas e pedidos por periodo, com os mesmos numeros do /dashboard. */
@Component
public class DashboardTools {
    private static final String AJUDA = "Use datas no formato yyyy-MM-dd, ou as palavras HOJE e ONTEM.";

    private final DashboardService dashboard;
    private final Clock clock;

    @Autowired
    public DashboardTools(DashboardService dashboard) {
        this(dashboard, Clock.system(DashboardService.ZONE));
    }

    DashboardTools(DashboardService dashboard, Clock clock) {
        this.dashboard = dashboard;
        this.clock = clock;
    }

    // nome em snake_case como as ferramentas do MCP: em camelCase o modelo misturava os estilos e inventava nomes
    @Tool(name = "resumo_por_periodo", description = "Vendas e pedidos de um periodo (hoje, ontem, semana, mes ou datas). "
            + "Pedidos contam pelo dia em que foram criados; receita e o total das comandas fechadas no periodo. "
            + "Datas no horario de Sao Paulo. Use sempre que a pergunta citar um periodo. "
            + "Aceita datas yyyy-MM-dd ou as palavras HOJE, ONTEM, SEMANA (ultimos 7 dias) e MES (do dia 1 ate hoje).")
    public Object resumoPorPeriodo(
            @ToolParam(description = "Data inicial: yyyy-MM-dd, HOJE, ONTEM, SEMANA ou MES") String de,
            @ToolParam(description = "Data final: yyyy-MM-dd, HOJE ou ONTEM. Omita para um dia so, SEMANA ou MES.", required = false) String ate) {
        // /dashboard e so ADMIN/MANAGER, mas /chat aceita tambem garcom e cozinha: a trava tem de estar aqui
        if (!canSeeRevenue()) {
            return erro("Sem permissao: vendas e receita so para ADMIN ou MANAGER.");
        }
        LocalDate today = LocalDate.now(clock.withZone(DashboardService.ZONE));
        String key = de == null ? "" : de.trim();
        LocalDate from;
        LocalDate to;
        if (key.equalsIgnoreCase("SEMANA")) {
            from = today.minusDays(6);
            to = today;
        } else if (key.equalsIgnoreCase("MES")) {
            from = today.withDayOfMonth(1);
            to = today;
        } else {
            try {
                from = parse(de);
                to = ate == null || ate.isBlank() ? from : parse(ate);
            } catch (DateTimeParseException e) {
                return erro("Data invalida. " + AJUDA);
            }
        }
        try {
            return dashboard.get(from, to);
        } catch (ResponseStatusException e) {
            return erro(String.valueOf(e.getReason()));
        }
    }

    private LocalDate parse(String value) {
        LocalDate today = LocalDate.now(clock.withZone(DashboardService.ZONE));
        String v = value == null ? "" : value.trim();
        if (v.isEmpty() || v.equalsIgnoreCase("HOJE")) return today;
        if (v.equalsIgnoreCase("ONTEM")) return today.minusDays(1);
        return LocalDate.parse(v);
    }

    private static boolean canSeeRevenue() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        for (GrantedAuthority a : auth.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_MANAGER")) return true;
        }
        return false;
    }

    private static Map<String, String> erro(String message) {
        return Map.of("erro", message);
    }
}
