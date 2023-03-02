package by.itacademy.account.service.impl;

import by.itacademy.account.dto.Account;
import by.itacademy.account.dto.Operation;
import by.itacademy.account.dto.Params;
import by.itacademy.account.service.OperationService;
import by.itacademy.account.utils.Generator;
import by.itacademy.account.exception.OptimisticLockException;
import by.itacademy.account.dao.OperationRepository;
import by.itacademy.account.constant.ParamSort;
import by.itacademy.account.dao.entity.AccountEntity;
import by.itacademy.account.dao.entity.OperationEntity;
import by.itacademy.account.service.AccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class OperationServiceImpl implements OperationService {

    private final int defaultDayInterval;
    private final OperationRepository operationRepository;
    private final AccountService accountService;
    private final ConversionService conversionService;
    private final Generator generator;

    public OperationServiceImpl(@Value("${default_day_interval}") int defaultDayInterval,
                                OperationRepository operationRepository,
                                AccountService accountService,
                                ConversionService conversionService,
                                Generator generator) {
        this.defaultDayInterval = defaultDayInterval;
        this.operationRepository = operationRepository;
        this.accountService = accountService;
        this.conversionService = conversionService;
        this.generator = generator;
    }

    @Transactional
    @Override
    public Operation add(UUID idAccount, Operation operation) {
        generateIdAndTimeAndAddToOperation(operation);
        Account account = accountService.get(idAccount);
        AccountEntity accountEntity = conversionService.convert(account, AccountEntity.class);
        OperationEntity operationEntity = conversionService.convert(operation, OperationEntity.class);
        operationEntity.setAccountEntity(accountEntity);
        OperationEntity savedOperation = operationRepository.save(operationEntity);
        Operation returnedOperation = conversionService.convert(savedOperation, Operation.class);
        returnedOperation.setAccount(account);
        return returnedOperation;
    }

    @Override
    public Page<Operation> get(UUID idAccount, Pageable pageable) {
        Page<OperationEntity> entities = operationRepository
                .findByAccountEntity_IdOrderByDtCreateAsc(idAccount, pageable);

        return convertToDtoPage(entities);
    }

    @Override
    public Page<Operation> getByParams(Params params, Pageable pageable) {
        fillFromAndToTimesIfAbsent(params);
        ParamSort sort = params.getSort();
        Pageable sortPageable = convertToSortPageable(sort, pageable);
        Specification<OperationEntity> specification = getSpecification(params);
        Page<OperationEntity> entities = operationRepository.findAll(specification, sortPageable);
        return convertToDtoWithAccountPage(entities);
    }

    @Override
    public Operation get(UUID idAccount, UUID idOperation) {
        return operationRepository
                .findByIdAndAccountEntity_Id(idOperation, idAccount)
                .map(entity -> conversionService.convert(entity, Operation.class))
                .orElse(null);
    }

    @Transactional
    @Override
    public Operation update(Operation operation, UUID idAccount, UUID idOperation, LocalDateTime dtUpdate) {
        OperationEntity entity = operationRepository
                .findByIdAndAccountEntity_Id(idOperation, idAccount)
                .orElse(null);

        if (entity == null) {
            return null;
        }

        checkDtUpdate(entity.getDtUpdate(), dtUpdate);
        updateOperationEntity(entity, operation);
        OperationEntity savedOperation = operationRepository.save(entity);
        return conversionService.convert(savedOperation, Operation.class);
    }

    @Transactional
    @Override
    public void delete(UUID idAccount, UUID idOperation, LocalDateTime dtUpdate) {
        operationRepository.findByIdAndAccountEntity_Id(idOperation, idAccount)
                .ifPresent(entity -> {
                    checkDtUpdate(entity.getDtUpdate(), dtUpdate);
                    operationRepository.delete(entity);
                });
    }

    private void generateIdAndTimeAndAddToOperation(Operation operation) {
        UUID uuid = generator.generateUUID();
        LocalDateTime now = generator.now();

        if (operation.getDate() == null) {
            operation.setDate(now);
        }
        operation.setId(uuid);
        operation.setDtCreate(now);
        operation.setDtUpdate(now);
    }

    private Page<Operation> convertToDtoPage(Page<OperationEntity> entities) {
        return entities.map(entity -> conversionService.convert(entity, Operation.class));
    }

    private void fillFromAndToTimesIfAbsent(Params params) {
        LocalDate to = params.getTo();
        LocalDate from = params.getFrom();
        if (to == null) {
            to = generator.now().toLocalDate();
            params.setTo(to);
        }
        if (from == null) {
            from = to.minusDays(defaultDayInterval);
            params.setFrom(from);
        }
    }

    private Pageable convertToSortPageable(ParamSort paramSort, Pageable pageable) {
        List<Sort.Order> orders = new ArrayList<>();
        if (paramSort == ParamSort.BY_DATE) {
            orders.add(new Sort.Order(Sort.Direction.ASC, "date"));
        } else if (paramSort == ParamSort.BY_CATEGORY) {
            orders.add(new Sort.Order(Sort.Direction.ASC, "category"));
            orders.add(new Sort.Order(Sort.Direction.ASC, "value"));
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
    }

    private Specification<OperationEntity> getSpecification(Params params) {
        Set<UUID> accounts = params.getAccounts();
        Set<UUID> categories = params.getCategories();
        LocalDate from = params.getFrom();
        LocalDate to = params.getTo();

        LocalDateTime timeFrom = LocalDateTime.of(from, LocalTime.MIN);
        LocalDateTime timeTo = LocalDateTime.of(to, LocalTime.MAX);
        Specification<OperationEntity> specification = Specification
                .where(OperationRepository.dateGreaterThan(timeFrom))
                .and(OperationRepository.dateLessThan(timeTo));

        if (!CollectionUtils.isEmpty(accounts)) {
            specification.and(OperationRepository.accountsIdIn(accounts));
        }
        if (!CollectionUtils.isEmpty(categories)) {
            specification.and(OperationRepository.categoriesIdIn(categories));
        }

        return specification;
    }

    private Page<Operation> convertToDtoWithAccountPage(Page<OperationEntity> entities) {
        return entities.map(operationEntity -> {
            Operation operation = conversionService.convert(operationEntity, Operation.class);
            UUID idAccount = operationEntity.getAccountEntity().getId();
            Account account = accountService.get(idAccount);
            operation.setAccount(account);
            return operation;
        });
    }

    private void checkDtUpdate(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1.compareTo(dateTime2) != 0) {
            throw new OptimisticLockException();
        }
    }



    private void updateOperationEntity(OperationEntity entity, Operation operation) {
        if (operation.getDate() != null) {
            entity.setDate(operation.getDate());
        }
        entity.setDescription(operation.getDescription());
        entity.setCategory(operation.getCategory());
        entity.setValue(operation.getValue());
        entity.setCurrency(operation.getCurrency());
    }
}
