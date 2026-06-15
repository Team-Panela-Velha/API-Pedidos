package com.pedidos.api_pedidos.service;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.TableEntity;
import com.pedidos.api_pedidos.dto.table.GenerateCodesResponse;
import com.pedidos.api_pedidos.dto.table.TableRequest;
import com.pedidos.api_pedidos.dto.table.TableResponse;
import com.pedidos.api_pedidos.exception.ConflictException;
import com.pedidos.api_pedidos.exception.ResourceNotFoundException;
import com.pedidos.api_pedidos.repository.TableRepository;
import com.pedidos.api_pedidos.repository.TabRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableService {

    private static final String CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final TableRepository repository;
    private final TabRepository tabRepository;

    public TableService(TableRepository repository, TabRepository tabRepository) {
        this.repository = repository;
        this.tabRepository = tabRepository;
    }

    /**
     * Cria a mesa gerando um `code` único aleatório. O código do body é ignorado.
     */
    public TableResponse create(TableRequest request) {
        TableEntity entity = new TableEntity(generateUniqueCode());
        entity = repository.save(entity);

        return new TableResponse(entity.getId(), entity.getCode());
    }

    /**
     * Atualiza a mesa. O `code` não pode ser alterado diretamente (use generate-codes);
     * como `code` é o único campo, esta operação apenas valida a existência da mesa.
     */
    public TableResponse update(Long id, TableRequest request) {
        TableEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada: " + id));

        return new TableResponse(entity.getId(), entity.getCode());
    }

    public List<TableResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(e -> new TableResponse(e.getId(), e.getCode()))
                .collect(Collectors.toList());
    }

    public TableResponse getById(Long id) {
        TableEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada: " + id));

        return new TableResponse(entity.getId(), entity.getCode());
    }

    public TableResponse getByCode(String code) {
        TableEntity entity = repository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada: " + code));

        return new TableResponse(entity.getId(), entity.getCode());
    }

    /**
     * Remove a mesa. 409 se houver comanda aberta para a mesa.
     */
    public void delete(Long id) {
        TableEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada: " + id));

        tabRepository.findByTableIdAndClosedFalse(entity.getId()).ifPresent(tab -> {
            throw new ConflictException("Não é possível remover: a mesa possui uma comanda aberta (id=" + tab.getId() + ")");
        });

        repository.deleteById(id);
    }

    /**
     * Gera novos códigos de login e logout, sobrescrevendo o anterior.
     * O `code` (login) é persistido; o código de logout é efêmero (não armazenado),
     * pois o logout efetivo é feito invalidando o token JWT (blacklist).
     */
    public GenerateCodesResponse generateCodes(Long id) {
        TableEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada: " + id));

        String loginCode = generateUniqueCode();
        entity.setCode(loginCode);
        repository.save(entity);

        String logoutCode = generateCode();
        return new GenerateCodesResponse(loginCode, logoutCode);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String generateUniqueCode() {
        String code;
        do {
            code = generateCode();
        } while (repository.findByCode(code).isPresent());
        return code;
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CODE_ALPHABET.charAt(RANDOM.nextInt(CODE_ALPHABET.length())));
        }
        return sb.toString();
    }
}
