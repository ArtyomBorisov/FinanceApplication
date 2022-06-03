package by.itacademy.account.scheduler.service;

import by.itacademy.account.scheduler.controller.web.controllers.utils.JwtTokenUtil;
import by.itacademy.account.scheduler.model.Operation;
import by.itacademy.account.scheduler.model.Schedule;
import by.itacademy.account.scheduler.model.ScheduledOperation;
import by.itacademy.account.scheduler.repository.api.IScheduledOperationRepository;
import by.itacademy.account.scheduler.repository.entity.ScheduledOperationEntity;
import by.itacademy.account.scheduler.service.api.*;
import org.springframework.beans.factory.annotation.Value;
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
public class ScheduledOperationService implements IScheduledOperationService {

    @Value("${account_url}")
    private String accountUrl;

    @Value("${classifier_currency_url}")
    private String currencyUrl;

    @Value("${classifier_category_url}")
    private String categoryUrl;

    private final IScheduledOperationRepository scheduledOperationRepository;
    private final ConversionService conversionService;
    private final RestTemplate restTemplate;
    private final ISchedulerService schedulerService;
    private final UserHolder userHolder;

    public ScheduledOperationService(IScheduledOperationRepository scheduledOperationRepository,
                                     ConversionService conversionService,
                                     ISchedulerService schedulerService,
                                     UserHolder userHolder) {
        this.scheduledOperationRepository = scheduledOperationRepository;
        this.conversionService = conversionService;
        this.schedulerService = schedulerService;
        this.userHolder = userHolder;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    @Override
    public ScheduledOperation add(ScheduledOperation scheduledOperation) {
        if (scheduledOperation == null) {
            throw new ValidationException(new ValidationError("scheduledOperation", MessageError.MISSING_OBJECT));
        }

        List<ValidationError> errors = new ArrayList<>();
        Operation operation = scheduledOperation.getOperation();
        Schedule schedule = scheduledOperation.getSchedule();

        this.checkOperation(operation, errors);
        this.checkSchedule(schedule, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        scheduledOperation.setId(id);
        scheduledOperation.setDtCreate(now);
        scheduledOperation.setDtUpdate(now);
        scheduledOperation.getOperation().setUser(this.userHolder.getLoginFromContext());

        ScheduledOperationEntity save;
        try {
            save = this.scheduledOperationRepository.save(
                    this.conversionService.convert(scheduledOperation, ScheduledOperationEntity.class));
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        this.schedulerService.addScheduledOperation(schedule, id);

        return this.conversionService.convert(save, ScheduledOperation.class);
    }

    @Override
    public ScheduledOperation get(UUID id) {
        String login = this.userHolder.getLoginFromContext();

        List<ValidationError> errors = new ArrayList<>();
        ScheduledOperationEntity entity;

        this.checkIdScheduledOperation(id, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        try {
            entity = this.scheduledOperationRepository.findByUserAndId(login, id).get();
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return this.conversionService.convert(entity, ScheduledOperation.class);
    }

    @Override
    public Page<ScheduledOperation> get(Pageable pageable) {
        String login = this.userHolder.getLoginFromContext();

        Page<ScheduledOperationEntity> entities;

        try {
            entities = this.scheduledOperationRepository.findByUserOrderByDtCreateAsc(login, pageable);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> this.conversionService.convert(entity, ScheduledOperation.class))
                .collect(Collectors.toList()), pageable, entities.getTotalElements());
    }

    @Transactional
    @Override
    public ScheduledOperation update(ScheduledOperation scheduledOperation, UUID id, LocalDateTime dtUpdate) {
        String login = this.userHolder.getLoginFromContext();

        if (scheduledOperation == null) {
            throw new ValidationException(new ValidationError("scheduledOperation", MessageError.MISSING_OBJECT));
        }

        List<ValidationError> errors = new ArrayList<>();
        ScheduledOperationEntity entity = null;
        Operation operation = scheduledOperation.getOperation();
        Schedule schedule = scheduledOperation.getSchedule();

        this.checkIdScheduledOperation(id, errors);
        this.checkOperation(operation, errors);
        this.checkSchedule(schedule, errors);

        if (id != null){
            try {
                entity = this.scheduledOperationRepository.findByUserAndId(login, id).orElse(null);
            } catch (Exception e) {
                throw new RuntimeException(MessageError.SQL_ERROR, e);
            }
        }

        if (dtUpdate == null) {
            errors.add(new ValidationError("dtUpdate", MessageError.MISSING_FIELD));
        } else if (entity != null && dtUpdate.compareTo(entity.getDtUpdate()) != 0) {
            errors.add(new ValidationError("dtUpdate", MessageError.INVALID_DT_UPDATE));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        entity.setStartTime(schedule.getStartTime());
        entity.setStopTime(schedule.getStopTime());
        entity.setInterval(schedule.getInterval());
        entity.setTimeUnit(schedule.getTimeUnit() == null ? null : schedule.getTimeUnit().toString());
        entity.setAccount(operation.getAccount());
        entity.setDescription(operation.getDescription());
        entity.setValue(operation.getValue());
        entity.setCurrency(operation.getCurrency());
        entity.setCategory(operation.getCategory());

        ScheduledOperationEntity save;
        try {
            save = this.scheduledOperationRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        this.schedulerService.deleteScheduledOperation(id);
        this.schedulerService.addScheduledOperation(schedule, id);

        return this.conversionService.convert(save, ScheduledOperation.class);
    }

    private void checkIdScheduledOperation(UUID idScheduledOperation, List<ValidationError> errors) {
        if (idScheduledOperation == null) {
            errors.add(new ValidationError("idScheduledOperation", MessageError.MISSING_FIELD));
            return;
        }

        try {
            if (!this.scheduledOperationRepository.existsById(idScheduledOperation)) {
                errors.add(new ValidationError("idScheduledOperation", MessageError.ID_NOT_EXIST));
            }
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }
    }

    private void checkSchedule(Schedule schedule, List<ValidationError> errors) {
        if (schedule == null) {
            errors.add(new ValidationError("schedule", MessageError.MISSING_OBJECT));
            return;
        }

        if (schedule.getInterval() < 0) {
            errors.add(new ValidationError("interval", "Интервал должен быть положительным"));
        } else if (schedule.getInterval() > 0 && schedule.getTimeUnit() == null) {
            errors.add(new ValidationError("timeUnit", MessageError.MISSING_OBJECT));
        }

        if (schedule.getStopTime() != null
                && schedule.getStartTime() != null
                && schedule.getStartTime().isAfter(schedule.getStopTime())) {
            errors.add(new ValidationError("startTime and stopTime",
                    "Дата окончания не может быть раньше даты начала"));
        }
    }

    private void checkOperation(Operation operation, List<ValidationError> errors) {
        if (operation == null) {
            errors.add(new ValidationError("operation", MessageError.MISSING_OBJECT));
            return;
        }

        String idAccount = "account (id счёта)";
        String idCategory = "category (id категории)";
        String idCurrency = "currency (id валюты)";

        String currencyClassifierUrl = this.currencyUrl + "/" + operation.getCurrency();
        String categoryClassifierUrl = this.categoryUrl + "/" + operation.getCategory();
        String accountServiceUrl = this.accountUrl + "/" + operation.getAccount();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        String token = JwtTokenUtil.generateAccessToken(this.userHolder.getUser());
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        if (operation.getAccount() == null) {
            errors.add(new ValidationError(idAccount, MessageError.MISSING_FIELD));
        } else {
            try {
                this.restTemplate.exchange(accountServiceUrl, HttpMethod.GET, entity, String.class);
            } catch (HttpStatusCodeException e) {
                errors.add(new ValidationError(idAccount, MessageError.ID_NOT_EXIST));
            }
        }

        if (operation.getCategory() == null) {
            errors.add(new ValidationError(idCategory, MessageError.MISSING_FIELD));
        } else {
            try {
                this.restTemplate.exchange(categoryClassifierUrl, HttpMethod.GET, entity, String.class);
            } catch (HttpStatusCodeException e) {
                errors.add(new ValidationError(idCategory, MessageError.ID_NOT_EXIST));
            }
        }

        if (operation.getValue() == 0) {
            errors.add(new ValidationError("value", "Передана нулевая сумма операции"));
        }

        if (operation.getCurrency() == null) {
            errors.add(new ValidationError(idCurrency, MessageError.MISSING_FIELD));
        } else {
            try {
                this.restTemplate.exchange(currencyClassifierUrl, HttpMethod.GET, entity, String.class);
            } catch (HttpStatusCodeException e) {
                errors.add(new ValidationError(idCurrency, MessageError.ID_NOT_EXIST));
            }
        }
    }
}
