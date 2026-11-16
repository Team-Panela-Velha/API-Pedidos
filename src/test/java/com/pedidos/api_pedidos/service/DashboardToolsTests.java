package com.pedidos.api_pedidos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.pedidos.api_pedidos.dto.dashboard.DashboardResponse;
import com.pedidos.api_pedidos.dto.dashboard.DashboardResponse.DailyPoint;
import com.pedidos.api_pedidos.dto.dashboard.DashboardResponse.DataQuality;
import com.pedidos.api_pedidos.dto.dashboard.DashboardResponse.Orders;
import com.pedidos.api_pedidos.dto.dashboard.DashboardResponse.Period;
import com.pedidos.api_pedidos.dto.dashboard.DashboardResponse.RankedItem;
import com.pedidos.api_pedidos.dto.dashboard.DashboardResponse.Revenue;
import com.pedidos.api_pedidos.dto.dashboard.DashboardResponse.StatusCount;
import com.pedidos.api_pedidos.dto.dashboard.DashboardResponse.Tabs;
import com.pedidos.api_pedidos.service.DashboardTools.ResumoPeriodo;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

class DashboardToolsTests {
    // 2040-01-02T01:30Z = 2040-01-01 22:30 em Sao Paulo: "hoje" ainda e dia 1, nao dia 2 (UTC)
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2040-01-02T01:30:00Z"), ZoneOffset.UTC);

    private DashboardService dashboard;
    private DashboardTools tools;

    @BeforeEach
    void setUp() {
        dashboard = mock(DashboardService.class);
        when(dashboard.get(any(), any())).thenAnswer(inv -> sample(inv.getArgument(0), inv.getArgument(1)));
        tools = new DashboardTools(dashboard, CLOCK);
        loginAs("ROLE_MANAGER");
    }

    @AfterEach
    void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    private static void loginAs(String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("u", "t", List.of(new SimpleGrantedAuthority(role))));
    }

    private static DashboardResponse sample(LocalDate from, LocalDate to) {
        List<DailyPoint> daily = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) daily.add(new DailyPoint(d, 4, new BigDecimal("42.00")));
        return new DashboardResponse(new Period(from, to, "America/Sao_Paulo"),
                new Orders(4, List.of(new StatusCount("RECEIVED", 1), new StatusCount("DELIVERED", 3))),
                new Revenue(new BigDecimal("42.00"), new BigDecimal("42.00"), new BigDecimal("3.00"), 1),
                new Tabs(2, new BigDecimal("140.30")),
                List.of(new RankedItem(1L, "Poke de Salmao", 2, new BigDecimal("77.80"))),
                List.of(new RankedItem(9L, "Pokes", 4, new BigDecimal("154.30"))),
                daily, new DataQuality(0, 0));
    }

    @Test
    void hojeEOntemUsamODiaDeSaoPauloENaoODiaUtc() {
        assertThat(tools.resumoPorPeriodo("HOJE", "HOJE")).isInstanceOf(ResumoPeriodo.class);
        verify(dashboard).get(LocalDate.parse("2040-01-01"), LocalDate.parse("2040-01-01"));

        tools.resumoPorPeriodo("ontem", "ontem");
        verify(dashboard).get(LocalDate.parse("2039-12-31"), LocalDate.parse("2039-12-31"));
    }

    @Test
    void datasExplicitasEPeriodoSemDataFinal() {
        tools.resumoPorPeriodo("2040-01-01", "2040-01-07");
        verify(dashboard).get(LocalDate.parse("2040-01-01"), LocalDate.parse("2040-01-07"));

        tools.resumoPorPeriodo("2040-01-05", null);
        verify(dashboard).get(LocalDate.parse("2040-01-05"), LocalDate.parse("2040-01-05"));
    }

    @Test
    void semDatasUsaHoje() {
        tools.resumoPorPeriodo(null, null);
        verify(dashboard).get(LocalDate.parse("2040-01-01"), LocalDate.parse("2040-01-01"));
    }

    @Test
    void dataInvalidaVoltaErroLegivelParaOModelo() {
        Object result = tools.resumoPorPeriodo("semana passada", "hoje");

        assertThat(result).isInstanceOf(Map.class);
        assertThat(((Map<?, ?>) result).get("erro").toString()).contains("yyyy-MM-dd");
        verifyNoInteractions(dashboard);
    }

    @Test
    void periodoRejeitadoPeloServicoVoltaErroLegivel() {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "O período deve ter de 1 a 367 dias"))
                .when(dashboard).get(any(), any());

        Object result = tools.resumoPorPeriodo("2030-01-01", "2040-01-01");

        assertThat(((Map<?, ?>) result).get("erro").toString()).contains("367");
    }

    @Test
    void periodoComDatasTrocadasEInvertidoEmVezDeFalhar() {
        // o modelo pequeno mandava de=HOJE, ate=ONTEM e o servico rejeitava
        assertThat(tools.resumoPorPeriodo("HOJE", "ONTEM")).isInstanceOf(ResumoPeriodo.class);
        verify(dashboard).get(LocalDate.parse("2039-12-31"), LocalDate.parse("2040-01-01"));
    }

    @Test
    void erroDoServicoTrazDicaDeComoCorrigir() {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "O período deve ter de 1 a 367 dias"))
                .when(dashboard).get(any(), any());

        Object result = tools.resumoPorPeriodo("2030-01-01", "2040-01-01");

        assertThat(((Map<?, ?>) result).get("erro").toString()).contains("de=HOJE");
    }

    @Test
    void garcomECozinhaNaoRecebemReceita() {
        for (String role : List.of("ROLE_WAITER", "ROLE_KITCHEN", "ROLE_TABLE")) {
            loginAs(role);
            Object result = tools.resumoPorPeriodo("HOJE", "HOJE");
            assertThat(((Map<?, ?>) result).get("erro").toString()).contains("ADMIN");
        }
        verifyNoInteractions(dashboard);
    }

    @Test
    void semAutenticacaoNaoRecebeReceita() {
        SecurityContextHolder.clearContext();
        assertThat(tools.resumoPorPeriodo("HOJE", "HOJE")).isInstanceOf(Map.class);
        verifyNoInteractions(dashboard);
    }

    @Test
    void adminTambemRecebe() {
        loginAs("ROLE_ADMIN");
        tools.resumoPorPeriodo("HOJE", "HOJE");
        verify(dashboard).get(LocalDate.parse("2040-01-01"), LocalDate.parse("2040-01-01"));
    }

    @Test
    void semanaSaoOsUltimosSeteDiasAteHoje() {
        tools.resumoPorPeriodo("SEMANA", null);
        verify(dashboard).get(LocalDate.parse("2039-12-26"), LocalDate.parse("2040-01-01"));
    }

    @Test
    void mesVaiDoDiaUmAteHoje() {
        // 2040-01-20T15:00Z = 12:00 em Sao Paulo
        DashboardTools t = new DashboardTools(dashboard, Clock.fixed(Instant.parse("2040-01-20T15:00:00Z"), ZoneOffset.UTC));
        t.resumoPorPeriodo("mes", null);
        verify(dashboard).get(LocalDate.parse("2040-01-01"), LocalDate.parse("2040-01-20"));
    }

    @Test
    void resultadoUsaNomesQueNaoPermitemConfundirTotalComPrecoUnitario() {
        ResumoPeriodo r = (ResumoPeriodo) tools.resumoPorPeriodo("HOJE", "HOJE");

        assertThat(r.periodo()).isEqualTo("2040-01-01");
        assertThat(r.pedidosCriadosNoPeriodo()).isEqualTo(4);
        assertThat(r.pedidosPorStatus()).containsEntry("RECEIVED", 1L).containsEntry("DELIVERED", 3L);
        assertThat(r.receitaTotal()).isEqualTo("R$ 42,00");
        assertThat(r.comandasFechadasNoPeriodo()).isEqualTo(1);
        assertThat(r.ticketMedioPorComanda()).isEqualTo("R$ 42,00");
        assertThat(r.receitaDeAdicionais()).isEqualTo("R$ 3,00");
        assertThat(r.comandasAbertasAgora()).isEqualTo(2);
        assertThat(r.valorEmAberto()).isEqualTo("R$ 140,30");
        assertThat(r.produtosMaisVendidos().getFirst().nome()).isEqualTo("Poke de Salmao");
        assertThat(r.produtosMaisVendidos().getFirst().unidadesVendidas()).isEqualTo(2);
        assertThat(r.produtosMaisVendidos().getFirst().totalVendido()).isEqualTo("R$ 77,80");
        assertThat(r.categoriasMaisVendidas().getFirst().nome()).isEqualTo("Pokes");
    }

    @Test
    void valoresVemProntosEmReaisNoPadraoBrasileiro() {
        // o modelo pequeno errava a formatacao ("R$ 42,00,00"): ele so copia o texto pronto
        DashboardResponse r = sample(LocalDate.parse("2040-01-01"), LocalDate.parse("2040-01-01"));
        DashboardResponse grande = new DashboardResponse(r.period(), r.orders(),
                new Revenue(new BigDecimal("1234567.5"), new BigDecimal("0"), BigDecimal.ZERO, 3),
                r.tabs(), r.topProducts(), r.topCategories(), r.daily(), r.dataQuality());
        doReturn(grande).when(dashboard).get(any(), any());

        ResumoPeriodo result = (ResumoPeriodo) tools.resumoPorPeriodo("HOJE", "HOJE");

        assertThat(result.receitaTotal()).isEqualTo("R$ 1.234.567,50");
        assertThat(result.ticketMedioPorComanda()).isEqualTo("R$ 0,00");
    }

    @Test
    void umDiaNaoTrazSerieDiariaEPeriodoLongoTraz() {
        assertThat(((ResumoPeriodo) tools.resumoPorPeriodo("HOJE", "HOJE")).porDia()).isEmpty();

        ResumoPeriodo semana = (ResumoPeriodo) tools.resumoPorPeriodo("SEMANA", null);
        assertThat(semana.periodo()).isEqualTo("2039-12-26 a 2040-01-01");
        assertThat(semana.porDia()).hasSize(7);
        assertThat(semana.porDia().getFirst().dia()).isEqualTo("2039-12-26");
    }
}
