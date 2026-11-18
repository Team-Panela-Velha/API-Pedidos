package com.pedidos.api_pedidos.config;

import com.pedidos.api_pedidos.service.TableService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Ao subir, numera as mesas que ainda nao tem numero (criadas antes da coluna existir).
 * Nas proximas subidas nao encontra nada e nao faz nada.
 */
@Component
public class TableNumberBackfill implements ApplicationRunner {

    private final TableService tableService;

    public TableNumberBackfill(TableService tableService) {
        this.tableService = tableService;
    }

    @Override
    public void run(ApplicationArguments args) {
        tableService.assignMissingNumbers();
    }
}
