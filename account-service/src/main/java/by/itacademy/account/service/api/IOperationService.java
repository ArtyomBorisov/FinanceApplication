package by.itacademy.account.service.api;

import by.itacademy.account.model.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public interface IOperationService {
    Operation add(UUID idAccount, Operation operation);
    Page<Operation> get(UUID idAccount, Pageable pageable);
    Page<Operation> getByParams(Map<String, Object> params, Pageable pageable);
    Operation get(UUID idAccount, UUID idOperation);
    Operation update(Operation operation, UUID idAccount, UUID idOperation, LocalDateTime dtUpdate);
    void delete(UUID idAccount, UUID idOperation, LocalDateTime dtUpdate);
}
