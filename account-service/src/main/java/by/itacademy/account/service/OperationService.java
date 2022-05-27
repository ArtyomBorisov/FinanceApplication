package by.itacademy.account.service;

import by.itacademy.account.model.Operation;
import by.itacademy.account.repository.api.IOperationRepository;
import by.itacademy.account.repository.api.ParamSort;
import by.itacademy.account.repository.entity.OperationEntity;
import by.itacademy.account.service.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OperationService implements IOperationService {

    @Value("${classifier_currency_url}")
    private String currencyUrl;

    @Value("${classifier_category_url}")
    private String categoryUrl;

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
        this.checkIdAccount(idAccount, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(Errors.INCORRECT_PARAMS, errors);
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
            throw new RuntimeException(Errors.SQL_ERROR.name(), e);
        }

        return this.get(idAccount, idOperation);
    }

    @Override
    public Page<Operation> get(UUID idAccount, Pageable pageable) {
        List<ValidationError> errors = new ArrayList<>();

        this.checkIdAccount(idAccount, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(Errors.INCORRECT_PARAMS, errors);
        }

        Page<OperationEntity> entities;

        try {
            entities = this.operationRepository.findByAccountEntity_IdOrderByDtCreateAsc(idAccount, pageable);
        } catch (Exception e) {
            throw new RuntimeException(Errors.SQL_ERROR.name(), e);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> this.conversionService.convert(entity, Operation.class))
                .collect(Collectors.toList()));
    }

    @Override
    public Page<Operation> getByParams(Map<String, Object> params, Pageable pageable) {
        Object type = params.get("type");
        Object accountsObj = params.get("accounts");
        Object categoriesObj = params.get("categories");
        Object fromDateObj = params.get("from");
        Object toDateObj = params.get("to");

        List<Sort.Order> orders = new ArrayList<>();
        List<UUID> accountsUuid = null;
        List<UUID> categoriesUuid = null;
        LocalDateTime from = null;
        LocalDateTime to = null;

        if (type instanceof String) {
            ParamSort paramSort = ParamSort.valueOf((String) type);

            switch (paramSort) {
                case BY_CATEGORY_NAME:
                    orders.add(new Sort.Order(Sort.Direction.ASC, "category"));
                    orders.add(new Sort.Order(Sort.Direction.ASC, "value"));
                    break;
                case BY_DATE_AND_TIME:
                    orders.add(new Sort.Order(Sort.Direction.ASC, "date"));
                    break;
                default:
                    throw new ValidationException(Errors.INCORRECT_DATA.toString());
            }
        } else {
            throw new ValidationException(Errors.INCORRECT_DATA.toString());
        }

        try {
            if (accountsObj instanceof Collection) {
                accountsUuid = ((Collection<?>) accountsObj).stream()
                        .map(uuid -> UUID.fromString((String) uuid))
                        .collect(Collectors.toList());
            } else if (accountsObj != null) {
                throw new ValidationException(Errors.INCORRECT_DATA.toString());
            }

            if (categoriesObj instanceof Collection) {
                categoriesUuid = ((Collection<?>) categoriesObj).stream()
                        .map(uuid -> UUID.fromString((String) uuid))
                        .collect(Collectors.toList());
            } else if (categoriesObj != null) {
                throw new ValidationException(Errors.INCORRECT_DATA.toString());
            }
        } catch (IllegalArgumentException e) {
            throw new ValidationException(Errors.INCORRECT_DATA.toString());
        }

        if (fromDateObj instanceof Number) {
            LocalDate dt = LocalDate.ofEpochDay((int) fromDateObj);
            from = LocalDateTime.of(dt, LocalTime.MIN);
        } else if (fromDateObj != null) {
            throw new ValidationException(Errors.INCORRECT_DATA.toString());
        }

        if (toDateObj instanceof Number) {
            LocalDate dt = LocalDate.ofEpochDay((int) toDateObj);
            to = LocalDateTime.of(dt, LocalTime.MAX);
        } else if (toDateObj != null) {
            throw new ValidationException(Errors.INCORRECT_DATA.toString());
        }

        if (from != null && to == null) {
            to = LocalDateTime.of(from.plusDays(90).toLocalDate(), LocalTime.MAX);
        } else if (from == null && to != null) {
            from = LocalDateTime.of(to.minusDays(90).toLocalDate(), LocalTime.MIN);
        } else if (from == null && to == null) {
            throw new ValidationException(Errors.INCORRECT_DATA.toString());
        }

        Page<OperationEntity> entities;

        if (!emptyOrNull(accountsUuid) && !emptyOrNull(categoriesUuid)) {
             entities = this.operationRepository
                    .findByAccountEntity_IdInAndCategoryInAndDateGreaterThanEqualAndDateLessThanEqual(
                            accountsUuid, categoriesUuid, from, to, Sort.by(orders), pageable);

        } else if (emptyOrNull(accountsUuid) && !emptyOrNull(categoriesUuid)) {
            entities = this.operationRepository
                    .findByCategoryInAndDateGreaterThanEqualAndDateLessThanEqual(
                            categoriesUuid, from, to, Sort.by(orders), pageable);

        } else if (!emptyOrNull(accountsUuid) && emptyOrNull(categoriesUuid)) {
            entities = this.operationRepository
                    .findByAccountEntity_IdInAndDateGreaterThanEqualAndDateLessThanEqual(
                            accountsUuid, from, to, Sort.by(orders), pageable);

        } else {
            entities = this.operationRepository
                    .findByDateGreaterThanEqualAndDateLessThanEqual(from, to, Sort.by(orders), pageable);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> this.conversionService.convert(entity, Operation.class))
                .collect(Collectors.toList()));
    }

    @Override
    public Operation get(UUID idAccount, UUID idOperation) {
        List<ValidationError> errors = new ArrayList<>();
        OperationEntity entity = null;

        this.checkIdAccount(idAccount, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(Errors.INCORRECT_PARAMS.toString(), errors);
        }

        try {
            entity = this.operationRepository.findByIdAndAccountEntity_Id(idOperation, idAccount).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(Errors.SQL_ERROR.toString(), e);
        }

        if (entity == null) {
            throw new ValidationException(Errors.INCORRECT_DATA.toString());
        }

        return this.conversionService.convert(entity, Operation.class);
    }

    @Transactional
    @Override
    public Operation update(Operation operation, UUID idAccount, UUID idOperation, LocalDateTime dtUpdate) {
        List<ValidationError> errors = new ArrayList<>();
        OperationEntity entity = null;

        this.checkOperation(operation, errors);

        if (this.checkIdAccount(idAccount, errors)) {
            this.checkIdOperation(idOperation, idAccount, errors);
        }

        try {
            entity = this.operationRepository.findByIdAndAccountEntity_Id(idOperation, idAccount).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(Errors.SQL_ERROR.toString(), e);
        }

        if (entity != null) {
            this.checkDtUpdate(dtUpdate, entity, errors);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(Errors.INCORRECT_PARAMS.toString(), errors);
        }

        if (operation.getDate() == null) {
            operation.setDate(LocalDateTime.now());
        }

        entity.setDate(operation.getDate());
        entity.setDescription(operation.getDescription());
        entity.setCategory(operation.getCategory());
        entity.setValue(operation.getValue());
        entity.setCurrency(operation.getCurrency());

        OperationEntity save = null;

        try {
            save = this.operationRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(Errors.SQL_ERROR.toString(), e);
        }

        return this.conversionService.convert(save, Operation.class);
    }

    @Transactional
    @Override
    public void delete(UUID idAccount, UUID idOperation, LocalDateTime dtUpdate) {
        List<ValidationError> errors = new ArrayList<>();
        OperationEntity entity = null;

        if (this.checkIdAccount(idAccount, errors)) {
            this.checkIdOperation(idOperation, idAccount, errors);
        }

        try {
            entity = this.operationRepository.findByIdAndAccountEntity_Id(idOperation, idAccount).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(Errors.SQL_ERROR.toString(), e);
        }

        if (entity != null) {
            this.checkDtUpdate(dtUpdate, entity, errors);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(Errors.INCORRECT_PARAMS.toString(), errors);
        }

        try {
            this.operationRepository.delete(entity);
        } catch (Exception e) {
            throw new RuntimeException(Errors.SQL_ERROR.toString(), e);
        }
    }

    private void checkOperation(Operation operation, List<ValidationError> errors) {
        if (operation == null) {
            errors.add(new ValidationError("operation", "Не передан объект operation"));
            return;
        }

        String currencyClassifierUrl = this.currencyUrl + "/" + operation.getCurrency();
        String categoryClassifierUrl = this.categoryUrl + "/" + operation.getCategory();
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
                                  UUID idAccount,
                                  List<ValidationError> errors) {
        if (idOperation == null) {
            errors.add(new ValidationError("idOperation", "Не передан id операции"));
            return;
        }

        try {
            if (idAccount != null
                    && this.operationRepository.findByIdAndAccountEntity_Id(idOperation, idAccount).isEmpty()) {

                errors.add(new ValidationError("idOperation", "Передан id не существующей операции"));
            }
        } catch (Exception e) {
            throw new RuntimeException(Errors.SQL_ERROR.toString(), e);
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

    private boolean checkIdAccount(UUID idAccount, List<ValidationError> errors) {
        if (idAccount == null) {
            errors.add(new ValidationError("idAccount", "Не передан id аккаунта"));
            return false;
        } else if (!this.accountService.isAccountExist(idAccount)) {
            errors.add(new ValidationError("idAccount", "Передан id несуществующего аккаунта"));
            return false;
        }
        return true;
    }

    private boolean emptyOrNull(List list) {
        return list == null || list.isEmpty();
    }
}
