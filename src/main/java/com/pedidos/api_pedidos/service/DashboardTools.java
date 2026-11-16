package com.pedidos.api_pedidos.service;

import com.pedidos.api_pedidos.dto.dashboard.DashboardResponse;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
            + "totalVendido dos produtos e a soma de todas as unidades, nao o preco de uma unidade. "
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
        if (from.isAfter(to)) {
            // o modelo pequeno as vezes manda de=HOJE, ate=ONTEM: inverte em vez de falhar
            LocalDate swap = from;
            from = to;
            to = swap;
        }
        try {
            return resumo(dashboard.get(from, to));
        } catch (ResponseStatusException e) {
            return erro(e.getReason() + ". Para um dia so use de=HOJE e omita ate.");
        }
    }

    /**
     * Resultado enviado ao modelo. Os nomes dizem exatamente o que cada numero e: com o JSON cru do dashboard
     * ("baseRevenue") o modelo lia o total de um produto como preco unitario ("R$ 77,80 cada").
     */
    public record ResumoPeriodo(String periodo, long pedidosCriadosNoPeriodo, Map<String, Long> pedidosPorStatus,
            String receitaTotal, long comandasFechadasNoPeriodo, String ticketMedioPorComanda,
            String receitaDeAdicionais, long comandasAbertasAgora, String valorEmAberto,
            List<Item> produtosMaisVendidos, List<Item> categoriasMaisVendidas, List<Dia> porDia) {
        /** totalVendido e a soma de TODAS as unidades vendidas, nao o preco de uma unidade. */
        public record Item(String nome, long unidadesVendidas, String totalVendido) {}

        public record Dia(String dia, long pedidos, String receita) {}
    }

    /** Valores ja em reais no padrao brasileiro: o modelo pequeno errava a formatacao ("R$ 42,00,00"). */
    private static String reais(BigDecimal value) {
        return String.format(Locale.of("pt", "BR"), "R$ %,.2f", value);
    }

    private static ResumoPeriodo resumo(DashboardResponse r) {
        Map<String, Long> porStatus = new LinkedHashMap<>();
        r.orders().byStatus().forEach(s -> porStatus.put(s.status(), s.count()));
        LocalDate from = r.period().from();
        LocalDate to = r.period().to();
        boolean umDia = from.equals(to);
        List<ResumoPeriodo.Dia> porDia = umDia ? List.of()
                : r.daily().stream().map(d -> new ResumoPeriodo.Dia(d.date().toString(), d.orders(), reais(d.revenue()))).toList();
        return new ResumoPeriodo(umDia ? from.toString() : from + " a " + to,
                r.orders().total(), porStatus,
                reais(r.revenue().total()), r.revenue().closedTabs(), reais(r.revenue().averageTicket()),
                reais(r.revenue().addOns()), r.tabs().open(), reais(r.tabs().openValue()),
                itens(r.topProducts()), itens(r.topCategories()), porDia);
    }

    private static List<ResumoPeriodo.Item> itens(List<DashboardResponse.RankedItem> items) {
        return items.stream().map(i -> new ResumoPeriodo.Item(i.name(), i.quantity(), reais(i.baseRevenue()))).toList();
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
