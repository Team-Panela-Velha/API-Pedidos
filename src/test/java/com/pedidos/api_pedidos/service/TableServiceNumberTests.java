package com.pedidos.api_pedidos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pedidos.api_pedidos.domain.entity.TableEntity;
import com.pedidos.api_pedidos.dto.table.TableRequest;
import com.pedidos.api_pedidos.dto.table.TableResponse;
import com.pedidos.api_pedidos.exception.UnprocessableException;
import com.pedidos.api_pedidos.exception.ConflictException;
import com.pedidos.api_pedidos.repository.TabRepository;
import com.pedidos.api_pedidos.repository.TableRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TableServiceNumberTests {

    private TableRepository repository;
    private TableService service;

    @BeforeEach
    void setUp() {
        repository = mock(TableRepository.class);
        service = new TableService(repository, mock(TabRepository.class), mock(AuditService.class));
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(TableEntity.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private static TableRequest request(Integer number) {
        TableRequest r = new TableRequest();
        r.setNumber(number);
        return r;
    }

    @Test
    void criarComNumeroMantemONumeroEGeraCodigoAleatorio() {
        TableResponse created = service.create(request(12));

        assertThat(created.getNumber()).isEqualTo(12);
        assertThat(created.getCode()).hasSize(6);
    }

    @Test
    void criarSemNumeroUsaOProximoDepoisDoMaior() {
        when(repository.maxActiveNumber()).thenReturn(7);

        assertThat(service.create(request(null)).getNumber()).isEqualTo(8);
        assertThat(service.create(null).getNumber()).isEqualTo(8);
    }

    @Test
    void numeroRepetidoEntreMesasAtivasDevolveConflito() {
        when(repository.existsByNumberAndDeletedAtIsNull(3)).thenReturn(true);

        assertThatThrownBy(() -> service.create(request(3))).isInstanceOf(ConflictException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void numeroZeroOuNegativoEInvalido() {
        assertThatThrownBy(() -> service.create(request(0))).isInstanceOf(UnprocessableException.class);
        assertThatThrownBy(() -> service.create(request(-1))).isInstanceOf(UnprocessableException.class);
    }

    @Test
    void editarTrocaONumeroSemMudarOCodigo() {
        TableEntity mesa = new TableEntity("ABC123");
        mesa.setNumber(1);
        when(repository.findByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(mesa));

        TableResponse updated = service.update(5L, request(4));

        assertThat(updated.getNumber()).isEqualTo(4);
        assertThat(updated.getCode()).isEqualTo("ABC123");
    }

    @Test
    void editarParaNumeroDeOutraMesaDevolveConflito() {
        TableEntity mesa = new TableEntity("ABC123");
        mesa.setNumber(1);
        when(repository.findByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(mesa));
        when(repository.existsByNumberAndDeletedAtIsNullAndIdNot(2, 5L)).thenReturn(true);

        assertThatThrownBy(() -> service.update(5L, request(2))).isInstanceOf(ConflictException.class);
    }

    @Test
    void editarSemNumeroMantemOAtual() {
        TableEntity mesa = new TableEntity("ABC123");
        mesa.setNumber(9);
        when(repository.findByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(mesa));

        assertThat(service.update(5L, request(null)).getNumber()).isEqualTo(9);
    }
}
