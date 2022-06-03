package by.itacademy.account.service;

import by.itacademy.account.controller.web.controllers.utils.JwtTokenUtil;
import by.itacademy.account.model.Account;
import by.itacademy.account.model.Operation;
import by.itacademy.account.repository.api.IOperationRepository;
import by.itacademy.account.repository.api.ParamSort;
import by.itacademy.account.repository.entity.AccountEntity;
import by.itacademy.account.repository.entity.OperationEntity;
import by.itacademy.account.service.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.*;
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
    private final UserHolder userHolder;

    public OperationService(IOperationRepository operationRepository,
                            IAccountService accountService,
                            ConversionService conversionService,
                            UserHolder userHolder) {
        this.operationRepository = operationRepository;
        this.accountService = accountService;
        this.conversionService = conversionService;
        this.userHolder = userHolder;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    @Override
    public Operation add(UUID idAccount, Operation operation) {
        List<ValidationError> errors = new ArrayList<>();

        this.checkOperation(operation, errors);
        this.checkIdAccount(idAccount, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        if (operation.getDate() == null) {
            operation.setDate(LocalDateTime.now());
        }

        UUID idOperation = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Account account = this.accountService.get(idAccount);
        AccountEntity accountEntity = this.conversionService.convert(account, AccountEntity.class);

        operation.setId(idOperation);
        operation.setDtCreate(now);
        operation.setDtUpdate(now);
        operation.setAccount(account);

        OperationEntity save;
        try {
            save = this.operationRepository.save(
                    this.conversionService.convert(operation, OperationEntity.class).setAccountEntity(accountEntity)
            );
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return this.conversionService.convert(save, Operation.class).setAccount(account);
    }

    @Override
    public Page<Operation> get(UUID idAccount, Pageable pageable) {
        List<ValidationError> errors = new ArrayList<>();

        this.checkIdAccount(idAccount, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Page<OperationEntity> entities;

        try {
            entities = this.operationRepository.findByAccountEntity_IdOrderByDtCreateAsc(idAccount, pageable);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        Account account = this.accountService.get(idAccount);

        return new PageImpl<>(entities.stream()
                .map(entity -> this.conversionService.convert(entity, Operation.class))
                .collect(Collectors.toList()), pageable, entities.getTotalElements());
    }

    @Override
    public Page<Operation> getByParams(Map<String, Object> params, Pageable pageable) {
        String login = this.userHolder.getLoginFromContext();

        String typeParam = "type";
        String accountsParam = "accounts";
        String categoriesParam = "categories";
        String fromParam = "from";
        String toParam = "to";

        Object type = params.get(typeParam);
        Object accountsObj = params.get(accountsParam);
        Object categoriesObj = params.get(categoriesParam);
        Object fromDateObj = params.get(fromParam);
        Object toDateObj = params.get(toParam);

        List<Sort.Order> orders = new ArrayList<>();
        List<UUID> accountsUuid = null;
        List<UUID> categoriesUuid = null;
        LocalDateTime from = null;
        LocalDateTime to = null;

        List<ValidationError> errors = new ArrayList<>();

        if (type instanceof String) {
            try {
                ParamSort paramSort = ParamSort.valueOf((String) type);

                switch (paramSort) {
                    case BY_CATEGORY:
                        orders.add(new Sort.Order(Sort.Direction.ASC, "category"));
                        orders.add(new Sort.Order(Sort.Direction.ASC, "value"));
                        break;
                    case BY_DATE:
                        orders.add(new Sort.Order(Sort.Direction.ASC, "date"));
                        break;
                    default:
                        throw new RuntimeException("Нет реализации сортировки");
                }
            } catch (IllegalArgumentException e) {
                errors.add(new ValidationError(typeParam, MessageError.INVALID_FORMAT));
            }
        } else if (type == null){
            errors.add(new ValidationError(typeParam, MessageError.MISSING_FIELD));
        } else {
            errors.add(new ValidationError(typeParam, MessageError.INVALID_FORMAT));
        }

        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(orders));

        try {
            if (accountsObj instanceof Collection) {
                accountsUuid = ((Collection<?>) accountsObj).stream()
                        .map(uuid -> UUID.fromString((String) uuid))
                        .collect(Collectors.toList());

                for (UUID id : accountsUuid) {
                    if (!this.accountService.isAccountExist(id)) {
                        errors.add(new ValidationError(id.toString(), MessageError.ID_NOT_EXIST));
                    }
                }
            } else if (accountsObj != null) {
                errors.add(new ValidationError(accountsParam, MessageError.INVALID_FORMAT));
            }

            if (categoriesObj instanceof Collection) {
                categoriesUuid = ((Collection<?>) categoriesObj).stream()
                        .map(uuid -> UUID.fromString((String) uuid))
                        .collect(Collectors.toList());

                HttpEntity<Object> entity = new HttpEntity<>(this.createHeaders());
                for (UUID id : categoriesUuid) {
                    this.checkCategoryId(id, entity, errors);
                }
            } else if (categoriesObj != null) {
                errors.add(new ValidationError(categoriesParam, MessageError.INVALID_FORMAT));
            }
        } catch (IllegalArgumentException e) {
            errors.add(new ValidationError("uuid", MessageError.INVALID_FORMAT));
        }

        if (fromDateObj instanceof Number) {
            LocalDate dt = LocalDate.ofEpochDay((int) fromDateObj);
            from = LocalDateTime.of(dt, LocalTime.MIN);
        } else if (fromDateObj != null) {
            errors.add(new ValidationError(fromParam, MessageError.INVALID_FORMAT));
        }

        if (toDateObj instanceof Number) {
            LocalDate dt = LocalDate.ofEpochDay((int) toDateObj);
            to = LocalDateTime.of(dt, LocalTime.MAX);
        } else if (toDateObj != null) {
            errors.add(new ValidationError(toParam, MessageError.INVALID_FORMAT));
        }

        if (from != null && to == null) {
            to = LocalDateTime.of(from.plusDays(90).toLocalDate(), LocalTime.MAX);
        } else if (from == null && to != null) {
            from = LocalDateTime.of(to.minusDays(90).toLocalDate(), LocalTime.MIN);
        } else if (from == null && to == null) {
            errors.add(new ValidationError(fromParam, MessageError.MISSING_FIELD));
            errors.add(new ValidationError(toParam, MessageError.MISSING_FIELD));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Page<OperationEntity> entities;

        if (!emptyOrNull(accountsUuid) && !emptyOrNull(categoriesUuid)) {
             entities = this.operationRepository
                    .findByAccountEntity_IdInAndCategoryInAndDateGreaterThanEqualAndDateLessThanEqual(
                            accountsUuid, categoriesUuid, from, to, sorted);

        } else if (emptyOrNull(accountsUuid) && !emptyOrNull(categoriesUuid)) {
            entities = this.operationRepository
                    .findByAccountEntity_UserAndCategoryInAndDateGreaterThanEqualAndDateLessThanEqual(
                            login, categoriesUuid, from, to, sorted);

        } else if (!emptyOrNull(accountsUuid) && emptyOrNull(categoriesUuid)) {
            entities = this.operationRepository
                    .findByAccountEntity_IdInAndDateGreaterThanEqualAndDateLessThanEqual(
                            accountsUuid, from, to, sorted);

        } else {
            entities = this.operationRepository
                    .findByAccountEntity_UserAndDateGreaterThanEqualAndDateLessThanEqual(login, from, to, sorted);
        }

        return new PageImpl<>(entities.stream()
                .map(operationEntity -> this.conversionService.convert(operationEntity, Operation.class)
                        .setAccount(this.accountService.get(operationEntity.getAccountEntity().getId())))
                .collect(Collectors.toList()), pageable, entities.getTotalElements());
    }

    @Override
    public Operation get(UUID idAccount, UUID idOperation) {
        List<ValidationError> errors = new ArrayList<>();
        OperationEntity entity = null;

        this.checkIdAccount(idAccount, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        try {
            entity = this.operationRepository.findByIdAndAccountEntity_Id(idOperation, idAccount).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        if (entity == null) {
            throw new ValidationException(new ValidationError("id операции", MessageError.ID_NOT_EXIST));
        }

        return this.conversionService.convert(entity, Operation.class)
                .setAccount(this.accountService.get(idAccount));
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
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        if (entity != null) {
            this.checkDtUpdate(dtUpdate, entity, errors);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
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
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return this.conversionService.convert(save, Operation.class)
                .setAccount(this.accountService.get(idAccount));
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
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        if (entity != null) {
            this.checkDtUpdate(dtUpdate, entity, errors);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        try {
            this.operationRepository.delete(entity);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }
    }

    private void checkOperation(Operation operation, List<ValidationError> errors) {
        if (operation == null) {
            errors.add(new ValidationError("operation", MessageError.MISSING_OBJECT));
            return;
        }

        String currencyClassifierUrl = this.currencyUrl + "/" + operation.getCurrency();

        HttpHeaders headers = this.createHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        if (operation.getCategory() == null) {
            errors.add(new ValidationError("category (категория)", MessageError.MISSING_FIELD));
        } else {
            this.checkCategoryId(operation.getCategory(), entity, errors);
        }

        if (operation.getValue() == 0) {
            errors.add(new ValidationError("value (сумма)", "Передана нулевая сумма операции"));
        }

        if (operation.getCurrency() == null) {
            errors.add(new ValidationError("currency (валюта)", MessageError.MISSING_FIELD));
        } else {
            try {
                this.restTemplate.exchange(currencyClassifierUrl, HttpMethod.GET, entity, String.class);
            } catch (HttpStatusCodeException e) {
                errors.add(new ValidationError("id currency (id валюты)", MessageError.ID_NOT_EXIST));
            }
        }
    }

    private void checkIdOperation(UUID idOperation,
                                  UUID idAccount,
                                  List<ValidationError> errors) {
        if (idOperation == null) {
            errors.add(new ValidationError("id operation", MessageError.MISSING_FIELD));
            return;
        }

        try {
            if (idAccount != null
                    && this.operationRepository.findByIdAndAccountEntity_Id(idOperation, idAccount).isEmpty()) {

                errors.add(new ValidationError("id operation", MessageError.ID_NOT_EXIST));
            }
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }
    }

    private void checkDtUpdate(LocalDateTime dtUpdate,
                               OperationEntity entity,
                               List<ValidationError> errors) {
        if (dtUpdate == null) {
            errors.add(new ValidationError("dtUpdate (параметр последнего обновления)", MessageError.MISSING_FIELD));
        } else if (entity != null && dtUpdate.compareTo(entity.getDtUpdate()) != 0) {
            errors.add(new ValidationError("dtUpdate", MessageError.INVALID_DT_UPDATE));
        }
    }

    private boolean checkIdAccount(UUID idAccount, List<ValidationError> errors) {
        if (idAccount == null) {
            errors.add(new ValidationError("id account (id счёта)", MessageError.MISSING_FIELD));
            return false;
        } else if (!this.accountService.isAccountExist(idAccount)) {
            errors.add(new ValidationError("id account (id счёта)", MessageError.ID_NOT_EXIST));
            return false;
        }
        return true;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = JwtTokenUtil.generateAccessToken(this.userHolder.getUser());
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return headers;
    }

    private boolean checkCategoryId(UUID id, HttpEntity<Object> entity, List<ValidationError> errors) {
        String categoryClassifierUrl = this.categoryUrl + "/" + id;

        try {
            this.restTemplate.exchange(categoryClassifierUrl, HttpMethod.GET, entity, String.class);
            return true;
        } catch (HttpStatusCodeException e) {
            errors.add(new ValidationError("id category (id категории)", MessageError.ID_NOT_EXIST));
            return false;
        }
    }

    private boolean emptyOrNull(List list) {
        return list == null || list.isEmpty();
    }
}
