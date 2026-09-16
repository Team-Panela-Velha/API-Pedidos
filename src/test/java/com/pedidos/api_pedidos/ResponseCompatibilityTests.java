package com.pedidos.api_pedidos;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pedidos.api_pedidos.dto.product.ProductResponse;
import com.pedidos.api_pedidos.dto.tab.TabResponse;

class ResponseCompatibilityTests {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void productResponseKeepsAppFieldsAndExposesAvailabilityToWeb() throws Exception {
        ProductResponse product = new ProductResponse(1L, "Poke", new BigDecimal("25.00"),
                "Descrição", "image.png", 2L, false);
        var json = mapper.readTree(mapper.writeValueAsString(product));

        assertThat(json.get("id").asLong()).isEqualTo(1L);
        assertThat(json.get("name").asText()).isEqualTo("Poke");
        assertThat(json.get("price").decimalValue()).isEqualByComparingTo("25.00");
        assertThat(json.get("categoryId").asLong()).isEqualTo(2L);
        assertThat(json.get("available").asBoolean()).isFalse();
    }

    @Test
    void tabResponseKeepsAppFieldsAndExposesOpeningDateToCards() throws Exception {
        Instant openedAt = Instant.parse("2026-09-16T12:00:00Z");
        TabResponse tab = new TabResponse(3L, new BigDecimal("42.50"), false, 4L, "A1", openedAt);
        var json = mapper.readTree(mapper.writeValueAsString(tab));

        assertThat(json.get("id").asLong()).isEqualTo(3L);
        assertThat(json.get("totalValue").decimalValue()).isEqualByComparingTo("42.50");
        assertThat(json.get("closed").asBoolean()).isFalse();
        assertThat(json.get("tableId").asLong()).isEqualTo(4L);
        assertThat(json.get("tableCode").asText()).isEqualTo("A1");
        assertThat(json.get("openedAt").asText()).startsWith("2026-09-16T12:00:00Z");
    }
}
