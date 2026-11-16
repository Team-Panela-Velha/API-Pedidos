package com.pedidos.api_pedidos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.pedidos.api_pedidos.dto.dashboard.DashboardResponse;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
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

    @Test
    void hojeEOntemUsamODiaDeSaoPauloENaoODiaUtc() {
        DashboardResponse response = mock(DashboardResponse.class);
        when(dashboard.get(any(), any())).thenReturn(response);

        assertThat(tools.resumoPorPeriodo("HOJE", "HOJE")).isSameAs(response);
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
        when(dashboard.get(any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "O período deve ter de 1 a 367 dias"));

        Object result = tools.resumoPorPeriodo("2030-01-01", "2040-01-01");

        assertThat(((Map<?, ?>) result).get("erro").toString()).contains("367");
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
}
