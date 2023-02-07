package by.itacademy.account.service;

import by.itacademy.account.dto.Operation;
import by.itacademy.account.dto.Params;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OperationService {
    Operation add(UUID idAccount, Operation operation);
    Page<Operation> get(UUID idAccount, Pageable pageable);
    Page<Operation> getByParams(Params params, Pageable pageable);
    Operation get(UUID idAccount, UUID idOperation);
    Operation update(Operation operation, UUID idAccount, UUID idOperation, LocalDateTime dtUpdate);
    void delete(UUID idAccount, UUID idOperation, LocalDateTime dtUpdate);
}
