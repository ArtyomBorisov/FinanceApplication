package by.itacademy.account.service;

import by.itacademy.account.controller.web.controllers.utils.JwtTokenUtil;
import by.itacademy.account.dto.Account;
import by.itacademy.account.dto.Operation;
import by.itacademy.account.exception.MessageError;
import by.itacademy.account.exception.ValidationError;
import by.itacademy.account.exception.ValidationException;
import by.itacademy.account.repository.api.IOperationRepository;
import by.itacademy.account.enums.ParamSort;
import by.itacademy.account.repository.entity.AccountEntity;
import by.itacademy.account.repository.entity.OperationEntity;
import by.itacademy.account.service.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
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

    @Value("${default_day_interval}")
    private int defaultDayInterval;

    private final IOperationRepository operationRepository;
    private final IAccountService accountService;
    private final ConversionService conversionService;
    private final RestTemplate restTemplate;
    private final UserHolder userHolder;

    private final String typeParam = "type";
    private final String accountsParam = "accounts";
    private final String categoriesParam = "categories";
    private final String fromParam = "from";
    private final String toParam = "to";

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

        checkOperation(operation, errors);
        checkIdAccount(idAccount, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        if (operation.getDate() == null) {
            operation.setDate(LocalDateTime.now());
        }

        UUID idOperation = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Account account = accountService.get(idAccount);
        AccountEntity accountEntity = conversionService.convert(account, AccountEntity.class);

        operation.setId(idOperation);
        operation.setDtCreate(now);
        operation.setDtUpdate(now);
        operation.setAccount(account);

        OperationEntity save;
        try {
            save = operationRepository.save(
                    conversionService.convert(operation, OperationEntity.class).setAccountEntity(accountEntity)
            );
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return conversionService.convert(save, Operation.class).setAccount(account);
    }

    @Override
    public Page<Operation> get(UUID idAccount, Pageable pageable) {
        checkIdAccount(idAccount);

        Page<OperationEntity> entities;

        try {
            entities = operationRepository.findByAccountEntity_IdOrderByDtCreateAsc(idAccount, pageable);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> conversionService.convert(entity, Operation.class))
                .collect(Collectors.toList()), pageable, entities.getTotalElements());
    }

    @Override
    public Page<Operation> getByParams(Map<String, Object> rawParams, Pageable pageable) {
        String login = userHolder.getLoginFromContext();

        Map<String, Object> checkedParams = checkParams(rawParams);

        List<Sort.Order> orders = (List<Sort.Order>) checkedParams.get(typeParam);
        Set<UUID> accountsUuidSet = (Set<UUID>) checkedParams.get(accountsParam);
        Set<UUID> categoriesUuidSet = (Set<UUID>) checkedParams.get(categoriesParam);
        LocalDateTime from = (LocalDateTime) checkedParams.get(fromParam);
        LocalDateTime to = (LocalDateTime) checkedParams.get(toParam);

        Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(orders));

        Page<OperationEntity> entities = operationRepository.findAll(Specification
                        .where(IOperationRepository.hasUser(login))
                        .and(IOperationRepository.accountsIdIn(accountsUuidSet))
                        .and(IOperationRepository.categoriesIdIn(categoriesUuidSet))
                        .and(IOperationRepository.dateGreaterThan(from))
                        .and(IOperationRepository.dateLessThan(to)),
                sorted);

        return new PageImpl<>(entities.stream()
                .map(operationEntity -> conversionService.convert(operationEntity, Operation.class)
                        .setAccount(accountService.get(operationEntity.getAccountEntity().getId())))
                .collect(Collectors.toList()), pageable, entities.getTotalElements());
    }

    @Override
    public Operation get(UUID idAccount, UUID idOperation) {
        this.checkIdAccount(idAccount);

        OperationEntity entity = null;

        try {
            entity = this.operationRepository.findByIdAndAccountEntity_Id(idOperation, idAccount).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        if (entity == null) {
            throw new ValidationException(new ValidationError("id операции", MessageError.ID_NOT_EXIST));
        }

        return conversionService.convert(entity, Operation.class)
                .setAccount(accountService.get(idAccount));
    }

    @Transactional
    @Override
    public Operation update(Operation operation, UUID idAccount, UUID idOperation, LocalDateTime dtUpdate) {
        checkOperation(operation);
        OperationEntity entity = get(idAccount, idOperation, dtUpdate);

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
            save = operationRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return conversionService.convert(save, Operation.class)
                .setAccount(accountService.get(idAccount));
    }

    @Transactional
    @Override
    public void delete(UUID idAccount, UUID idOperation, LocalDateTime dtUpdate) {
        OperationEntity entity = get(idAccount, idOperation, dtUpdate);

        try {
            operationRepository.delete(entity);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }
    }

    private Map<String, Object> checkParams(Map<String, Object> rawParams) {
        Map<String, Object> checkedParams = new HashMap<>();

        Object typeObj = rawParams.get(typeParam);
        Object accountsObj = rawParams.get(accountsParam);
        Object categoriesObj = rawParams.get(categoriesParam);
        Object fromDateObj = rawParams.get(fromParam);
        Object toDateObj = rawParams.get(toParam);

        List<Sort.Order> orders = new ArrayList<>();
        List<ValidationError> errors = new ArrayList<>();

        if (typeObj instanceof String) {
            try {
                ParamSort paramSort = ParamSort.valueOf((String) typeObj);

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
        } else if (typeObj == null){
            errors.add(new ValidationError(typeParam, MessageError.MISSING_FIELD));
        } else {
            errors.add(new ValidationError(typeParam, MessageError.INVALID_FORMAT));
        }

        Set<UUID> accountsUuidSet = null;
        Set<UUID> categoriesUuidSet = null;

        try {
            if (accountsObj instanceof Collection) {
                accountsUuidSet = ((Collection<?>) accountsObj).stream()
                        .map(uuid -> UUID.fromString((String) uuid))
                        .collect(Collectors.toSet());

                for (UUID id : accountsUuidSet) {
                    if (!this.accountService.isAccountExist(id)) {
                        errors.add(new ValidationError(id.toString(), MessageError.ID_NOT_EXIST));
                    }
                }
            } else if (accountsObj != null) {
                errors.add(new ValidationError(accountsParam, MessageError.INVALID_FORMAT));
            }

            if (categoriesObj instanceof Collection) {
                categoriesUuidSet = ((Collection<?>) categoriesObj).stream()
                        .map(uuid -> UUID.fromString((String) uuid))
                        .collect(Collectors.toSet());

                HttpEntity<Object> entity = new HttpEntity<>(this.createHeaders());
                for (UUID id : categoriesUuidSet) {
                    checkCategoryId(id, entity, errors);
                }
            } else if (categoriesObj != null) {
                errors.add(new ValidationError(categoriesParam, MessageError.INVALID_FORMAT));
            }
        } catch (IllegalArgumentException e) {
            errors.add(new ValidationError("uuid", MessageError.INVALID_FORMAT));
        }

        LocalDateTime from = checkParamDate(fromDateObj, LocalTime.MIN, fromParam, errors);
        LocalDateTime to = checkParamDate(toDateObj, LocalTime.MAX, toParam, errors);

        if (from != null && to == null) {
            to = LocalDateTime.of(from.plusDays(defaultDayInterval).toLocalDate(), LocalTime.MAX);
        } else if (from == null && to != null) {
            from = LocalDateTime.of(to.minusDays(defaultDayInterval).toLocalDate(), LocalTime.MIN);
        } else if (from == null && to == null) {
            errors.add(new ValidationError(fromParam + ", " + toParam,
                    "Требуется передать как минимум один параметр из указанных"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        checkedParams.put(typeParam, orders);
        checkedParams.put(accountsParam, accountsUuidSet);
        checkedParams.put(categoriesParam, categoriesUuidSet);
        checkedParams.put(fromParam, from);
        checkedParams.put(toParam, to);

        return checkedParams;
    }

    private LocalDateTime checkParamDate(Object obj, LocalTime time, String paramName, List<ValidationError> errors) {
        LocalDateTime ldt = null;

        if (obj instanceof Number) {
            LocalDate dt = LocalDate.ofEpochDay((int) obj);
            ldt = LocalDateTime.of(dt, time);
        } else if (obj != null) {
            errors.add(new ValidationError(paramName, MessageError.INVALID_FORMAT));
        }

        return ldt;
    }

    private OperationEntity get(UUID idAccount, UUID idOperation, LocalDateTime dtUpdate) {
        List<ValidationError> errors = new ArrayList<>();
        OperationEntity entity = null;

        if (checkIdAccount(idAccount, errors)) {
            if (idOperation == null) {
                errors.add(new ValidationError("id operation", MessageError.MISSING_FIELD));
            } else {
                try {
                    if (idAccount != null
                            && operationRepository.findByIdAndAccountEntity_Id(idOperation, idAccount).isEmpty()) {

                        errors.add(new ValidationError("id operation", MessageError.ID_NOT_EXIST));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(MessageError.SQL_ERROR, e);
                }
            }
        }

        try {
            entity = operationRepository.findByIdAndAccountEntity_Id(idOperation, idAccount).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        if (entity != null && dtUpdate == null) {
            errors.add(new ValidationError("dtUpdate (параметр последнего обновления)", MessageError.MISSING_FIELD));
        } else if (entity != null && dtUpdate.compareTo(entity.getDtUpdate()) != 0) {
            errors.add(new ValidationError("dtUpdate", MessageError.INVALID_DT_UPDATE));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return entity;
    }

    private void checkOperation(Operation operation, List<ValidationError> errors) {
        if (operation == null) {
            errors.add(new ValidationError("operation", MessageError.MISSING_OBJECT));
            return;
        }

        String currencyClassifierUrl = currencyUrl + "/" + operation.getCurrency();

        HttpHeaders headers = createHeaders();
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        if (operation.getCategory() == null) {
            errors.add(new ValidationError("category (категория)", MessageError.MISSING_FIELD));
        } else {
            checkCategoryId(operation.getCategory(), entity, errors);
        }

        if (operation.getValue() == 0) {
            errors.add(new ValidationError("value (сумма)", "Передана нулевая сумма операции"));
        }

        if (operation.getCurrency() == null) {
            errors.add(new ValidationError("currency (валюта)", MessageError.MISSING_FIELD));
        } else {
            try {
                restTemplate.exchange(currencyClassifierUrl, HttpMethod.GET, entity, String.class);
            } catch (HttpStatusCodeException e) {
                errors.add(new ValidationError("id currency (id валюты)", MessageError.ID_NOT_EXIST));
            }
        }
    }

    private void checkOperation(Operation operation) {
        List<ValidationError> errors = new ArrayList<>();

        checkOperation(operation, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private boolean checkIdAccount(UUID idAccount, List<ValidationError> errors) {
        if (idAccount == null) {
            errors.add(new ValidationError("id account (id счёта)", MessageError.MISSING_FIELD));
            return false;
        } else if (!accountService.isAccountExist(idAccount)) {
            errors.add(new ValidationError("id account (id счёта)", MessageError.ID_NOT_EXIST));
            return false;
        }
        return true;
    }

    private void checkIdAccount(UUID idAccount) {
        List<ValidationError> errors = new ArrayList<>();

        checkIdAccount(idAccount, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = JwtTokenUtil.generateAccessToken(userHolder.getUser());
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return headers;
    }

    private boolean checkCategoryId(UUID id, HttpEntity<Object> entity, List<ValidationError> errors) {
        String categoryClassifierUrl = categoryUrl + "/" + id;

        try {
            restTemplate.exchange(categoryClassifierUrl, HttpMethod.GET, entity, String.class);
            return true;
        } catch (HttpStatusCodeException e) {
            errors.add(new ValidationError("id category (id категории)", MessageError.ID_NOT_EXIST));
            return false;
        }
    }
}
