package by.itacademy.account.service;

import by.itacademy.account.model.Operation;
import by.itacademy.account.repository.api.IOperationRepository;
import by.itacademy.account.repository.entity.OperationEntity;
import by.itacademy.account.service.api.IAccountService;
import by.itacademy.account.service.api.IOperationService;
import by.itacademy.account.service.api.ValidationError;
import by.itacademy.account.service.api.ValidationException;
import com.sun.istack.NotNull;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OperationService implements IOperationService {

    private final IOperationRepository operationRepository;
    private final IAccountService accountService;
    private final ConversionService conversionService;
    private final RestTemplate restTemplate;

    public OperationService(IOperationRepository operationRepository,
                            IAccountService accountService,
                            ConversionService conversionService) {
        this.operationRepository = operationRepository;
        this.accountService = accountService;
        this.conversionService = conversionService;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    @Override
    public Operation add(UUID idAccount, Operation operation) {
        List<ValidationError> errors = new ArrayList<>();

        this.checkOperation(operation, errors);

        if (idAccount == null) {
            errors.add(new ValidationError("idAccount", "Не передан id счёта"));
        } else if (!this.accountService.isAccountExist(idAccount)) {
            errors.add(new ValidationError("idAccount", "Передан id несуществующего счёта"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Переданы некорректные параметры", errors);
        }

        if (operation.getDate() == null) {
            operation.setDate(LocalDateTime.now());
        }

        UUID idOperation = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        operation.setId(idOperation);
        operation.setDtCreate(now);
        operation.setDtUpdate(now);
        operation.setAccount(this.accountService.get(idAccount));

        try {
            this.operationRepository.save(
                    this.conversionService.convert(operation, OperationEntity.class));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        return this.get(idAccount, idOperation);
    }

    @Override
    public Page<Operation> get(UUID idAccount, Pageable pageable) {
        List<ValidationError> errors = new ArrayList<>();

        if (idAccount == null) {
            errors.add(new ValidationError("idAccount", "Не передан id аккаунта"));
        } else if (!this.accountService.isAccountExist(idAccount)) {
            errors.add(new ValidationError("idAccount", "Передан id несуществующего аккаунта"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Переданы некорректные параметры", errors);
        }

        List<Operation> operationList = new ArrayList<>();
        List<OperationEntity> tempList;

        try {
            tempList = this.operationRepository.findByAccountEntity_IdOrderByDtCreateAsc(idAccount);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        if (!tempList.isEmpty()) {
            operationList = tempList.stream()
                    .map(entity -> this.conversionService.convert(entity, Operation.class))
                    .collect(Collectors.toList());
        }

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), operationList.size());
        return new PageImpl<>(operationList.subList(start, end), pageable, operationList.size());
    }

    @Override
    public Operation get(UUID idAccount, UUID idOperation) {
        List<ValidationError> errors = new ArrayList<>();
        OperationEntity entity = null;

        if (idAccount == null) {
            errors.add(new ValidationError("idAccount", "Не передан id аккаунта"));
        } else if (!this.accountService.isAccountExist(idAccount)) {
            errors.add(new ValidationError("idAccount", "Передан id несуществующего аккаунта"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Переданы некорректные параметры", errors);
        }

        try {
            entity = this.operationRepository.findByIdAndAccountEntity_Id(idOperation, idAccount).get();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        return this.conversionService.convert(entity, Operation.class);
    }

    @Transactional
    @Override
    public Operation update(Operation operation, UUID idAccount, UUID idOperation, LocalDateTime dtUpdate) {
        List<ValidationError> errors = new ArrayList<>();
        OperationEntity entity = null;

        this.checkOperation(operation, errors);
        this.checkIdOperation(idOperation, idAccount, errors);

        try {
            entity = this.operationRepository.findByIdAndAccountEntity_Id(idOperation, idAccount).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        this.checkDtUpdate(dtUpdate, entity, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Переданы некорректные параметры", errors);
        }

        entity.setDate(operation.getDate());
        entity.setDescription(operation.getDescription());
        entity.setCategory(operation.getCategory());
        entity.setValue(operation.getValue());
        entity.setCurrency(operation.getCurrency());

        try {
            this.operationRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        return this.get(idAccount, idOperation);
    }

    @Transactional
    @Override
    public void delete(UUID idAccount, UUID idOperation, LocalDateTime dtUpdate) {
        List<ValidationError> errors = new ArrayList<>();
        OperationEntity entity = null;

        if (idAccount == null) {
            errors.add(new ValidationError("idAccount", "Не передан id аккаунта"));
        } else if (!this.accountService.isAccountExist(idAccount)) {
            errors.add(new ValidationError("idAccount", "Передан id несуществующего аккаунта"));
        }

        this.checkIdOperation(idOperation, idAccount, errors);

        try {
            entity = this.operationRepository.findByIdAndAccountEntity_Id(idOperation, idAccount).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        this.checkDtUpdate(dtUpdate, entity, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Переданы некорректные параметры", errors);
        }

        try {
            this.operationRepository.delete(entity);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }
    }

    private void checkOperation(Operation operation, List<ValidationError> errors) {
        if (operation == null) {
            errors.add(new ValidationError("operation", "Не передан объект operation"));
            return;
        }

        String currencyClassifierUrl = "http://localhost:8081/classifier/currency/" + operation.getCurrency() + "/";
        String categoryClassifierUrl = "http://localhost:8081/classifier/operation/category/" + operation.getCategory() + "/";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        if (operation.getCategory() == null) {
            errors.add(new ValidationError("category", "Не передана категория операции"));
        } else {
            try {
                this.restTemplate.exchange(categoryClassifierUrl, HttpMethod.GET, entity, String.class);
            } catch (HttpStatusCodeException e) {
                errors.add(new ValidationError("category", "Передан id категории, которой нет в справочнике"));
            }
        }

        if (operation.getValue() == 0) {
            errors.add(new ValidationError("value", "Передана нулевая сумма операции"));
        }

        if (operation.getCurrency() == null) {
            errors.add(new ValidationError("currency", "Не передана валюта операции"));
        } else {
            try {
                this.restTemplate.exchange(currencyClassifierUrl, HttpMethod.GET, entity, String.class);
            } catch (HttpStatusCodeException e) {
                errors.add(new ValidationError("currency", "Передан id валюты, которой нет в справочнике"));
            }
        }
    }

    private void checkIdOperation(UUID idOperation,
                                  @NotNull UUID idAccount,
                                  List<ValidationError> errors) {
        if (idOperation == null) {
            errors.add(new ValidationError("idOperation", "Не передан id операции"));
            return;
        }

        try {
            if (this.operationRepository.findByIdAndAccountEntity_Id(idOperation, idAccount).isEmpty()) {
                errors.add(new ValidationError("idOperation", "Передан id не существующей операции"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }
    }

    private void checkDtUpdate(LocalDateTime dtUpdate,
                               OperationEntity entity,
                               List<ValidationError> errors) {
        if (dtUpdate == null) {
            errors.add(new ValidationError("dtUpdate", " Не передан параметр последнего обновления"));
        } else if (entity != null && dtUpdate.compareTo(entity.getDtUpdate()) != 0) {
            errors.add(new ValidationError("dtUpdate", "Передан неправильный параметр последнего обновления"));
        }
    }
}
