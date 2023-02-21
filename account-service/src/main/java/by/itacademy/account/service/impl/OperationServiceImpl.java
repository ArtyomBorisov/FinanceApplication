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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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
        UUID idOperation = generator.generateUUID();
        LocalDateTime now = generator.now();

        Account account = accountService.get(idAccount);
        AccountEntity accountEntity = conversionService.convert(account, AccountEntity.class);

        if (operation.getDate() == null) {
            operation.setDate(now);
        }

        operation.setId(idOperation);
        operation.setDtCreate(now);
        operation.setDtUpdate(now);

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

        List<Operation> operations = entities.stream()
                .map(entity -> conversionService.convert(entity, Operation.class))
                .collect(Collectors.toList());

        return new PageImpl<>(operations, pageable, entities.getTotalElements());
    }

    @Override
    public Page<Operation> getByParams(Params params, Pageable pageable) {
        Set<UUID> accounts = params.getAccounts();
        Set<UUID> categories = params.getCategories();

        fillFromAndToTimesIfAbsent(params);
        LocalDate from = params.getFrom();
        LocalDate to = params.getTo();

        ParamSort sort = params.getSort();
        List<Sort.Order> orders = getSort(sort);

        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(orders));

        LocalDateTime timeFrom = LocalDateTime.of(from, LocalTime.MIN);
        LocalDateTime timeTo = LocalDateTime.of(to, LocalTime.MAX);
        Specification<OperationEntity> specification = Specification
                .where(OperationRepository.dateGreaterThan(timeFrom))
                .and(OperationRepository.dateLessThan(timeTo));

        if (accounts != null && !accounts.isEmpty()) {
            specification.and(OperationRepository.accountsIdIn(accounts));
        }
        if (categories != null && !categories.isEmpty()) {
            specification.and(OperationRepository.categoriesIdIn(categories));
        }

        Page<OperationEntity> entities = operationRepository.findAll(specification, sorted);

        List<Operation> operations = entities.stream()
                .map(operationEntity -> {
                    Operation operation = conversionService.convert(operationEntity, Operation.class);
                    UUID idAccount = operationEntity.getAccountEntity().getId();
                    Account account = accountService.get(idAccount);
                    operation.setAccount(account);
                    return operation;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(operations, pageable, entities.getTotalElements());
    }

    @Override
    public Operation get(UUID idAccount, UUID idOperation) {
        OperationEntity entity = operationRepository
                .findByIdAndAccountEntity_Id(idOperation, idAccount)
                .orElse(null);

        return entity != null ? conversionService.convert(entity, Operation.class) : null;
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

        if (operation.getDate() != null) {
            entity.setDate(operation.getDate());
        }
        entity.setDescription(operation.getDescription());
        entity.setCategory(operation.getCategory());
        entity.setValue(operation.getValue());
        entity.setCurrency(operation.getCurrency());

        OperationEntity savedOperation = operationRepository.save(entity);
        return conversionService.convert(savedOperation, Operation.class);
    }

    @Transactional
    @Override
    public void delete(UUID idAccount, UUID idOperation, LocalDateTime dtUpdate) {
        OperationEntity entity = operationRepository
                .findByIdAndAccountEntity_Id(idOperation, idAccount)
                .orElse(null);

        if (entity == null) {
            return;
        }

        checkDtUpdate(entity.getDtUpdate(), dtUpdate);
        operationRepository.delete(entity);
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

    private void checkDtUpdate(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1.compareTo(dateTime2) != 0) {
            throw new OptimisticLockException();
        }
    }

    private List<Sort.Order> getSort(ParamSort paramSort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (paramSort == ParamSort.BY_DATE) {
            orders.add(new Sort.Order(Sort.Direction.ASC, "date"));
        } else if (paramSort == ParamSort.BY_CATEGORY) {
            orders.add(new Sort.Order(Sort.Direction.ASC, "category"));
            orders.add(new Sort.Order(Sort.Direction.ASC, "value"));
        }
        return orders;
    }
}
