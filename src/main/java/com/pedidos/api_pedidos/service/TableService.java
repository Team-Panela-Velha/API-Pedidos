package com.pedidos.api_pedidos.service;

import org.springframework.stereotype.Service;

import com.pedidos.api_pedidos.domain.entity.TableEntity;
import com.pedidos.api_pedidos.dto.table.GenerateCodesResponse;
import com.pedidos.api_pedidos.dto.table.TableRequest;
import com.pedidos.api_pedidos.dto.table.TableResponse;
import com.pedidos.api_pedidos.exception.ConflictException;
import com.pedidos.api_pedidos.exception.ResourceNotFoundException;
import com.pedidos.api_pedidos.exception.UnprocessableException;
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
    private final AuditService auditService;

    public TableService(TableRepository repository, TabRepository tabRepository, AuditService auditService) {
        this.repository = repository;
        this.tabRepository = tabRepository;
        this.auditService = auditService;
    }

    /**
     * Cria a mesa gerando um `code` único aleatório (o código do body é ignorado).
     * O `number` enviado é mantido; sem ele, a mesa recebe o próximo número depois do maior em uso.
     */
    public TableResponse create(TableRequest request) {
        Integer number = request != null ? request.getNumber() : null;
        if (number == null) {
            number = repository.maxActiveNumber() + 1;
        } else {
            validateNumber(number);
            if (repository.existsByNumberAndDeletedAtIsNull(number)) {
                throw new ConflictException("Já existe uma mesa com o número " + number);
            }
        }
        TableEntity entity = new TableEntity(generateUniqueCode());
        entity.setNumber(number);
        entity = repository.save(entity);

        return toResponse(entity);
    }

    /**
     * Atualiza o número da mesa. O `code` não pode ser alterado diretamente (use generate-codes).
     * Sem `number` no body, nada muda.
     */
    public TableResponse update(Long id, TableRequest request) {
        TableEntity entity = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada: " + id));

        Integer number = request != null ? request.getNumber() : null;
        if (number != null && !number.equals(entity.getNumber())) {
            validateNumber(number);
            if (repository.existsByNumberAndDeletedAtIsNullAndIdNot(number, id)) {
                throw new ConflictException("Já existe uma mesa com o número " + number);
            }
            entity.setNumber(number);
            entity = repository.save(entity);
        }
        return toResponse(entity);
    }

    /** Mesas antigas (sem número) recebem os próximos números livres, na ordem do id. */
    public void assignMissingNumbers() {
        int next = repository.maxActiveNumber();
        for (TableEntity entity : repository.findAllByDeletedAtIsNullAndNumberIsNullOrderByIdAsc()) {
            entity.setNumber(++next);
            repository.save(entity);
        }
    }

    private static void validateNumber(Integer number) {
        if (number < 1) {
            throw new UnprocessableException("O número da mesa deve ser maior que zero");
        }
    }

    public List<TableResponse> getAll() {
        return repository.findAllByDeletedAtIsNull()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TableResponse getById(Long id) {
        TableEntity entity = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada: " + id));

        return toResponse(entity);
    }

    public TableResponse getByCode(String code) {
        TableEntity entity = repository.findByCodeAndDeletedAtIsNull(code)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada: " + code));

        return toResponse(entity);
    }

    /**
     * Remove a mesa. 409 se houver comanda aberta para a mesa.
     */
    public void delete(Long id) {
        TableEntity entity = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada: " + id));

        tabRepository.findByTableIdAndClosedFalse(entity.getId()).ifPresent(tab -> {
            throw new ConflictException("Não é possível remover: a mesa possui uma comanda aberta (id=" + tab.getId() + ")");
        });

        entity.markDeleted(auditService.currentUserId());
        repository.save(entity);
    }

    /**
     * Gera novos códigos de login e logout, sobrescrevendo o anterior.
     * O `code` (login) é persistido; o código de logout é efêmero (não armazenado),
     * pois o logout efetivo é feito invalidando o token JWT (blacklist).
     */
    public GenerateCodesResponse generateCodes(Long id) {
        TableEntity entity = repository.findByIdAndDeletedAtIsNull(id)
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
        } while (repository.existsByCode(code));
        return code;
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CODE_ALPHABET.charAt(RANDOM.nextInt(CODE_ALPHABET.length())));
        }
        return sb.toString();
    }

    private TableResponse toResponse(TableEntity entity) {
        return new TableResponse(entity.getId(), entity.getCode(), entity.getNumber(),
                entity.getCreatedAt(), entity.getCreatedBy(), entity.getUpdatedAt(), entity.getUpdatedBy(),
                entity.getDeletedAt(), entity.getDeletedBy());
    }
}
