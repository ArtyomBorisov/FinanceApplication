package by.itacademy.account.scheduler.service;

import by.itacademy.account.scheduler.model.Operation;
import by.itacademy.account.scheduler.model.Schedule;
import by.itacademy.account.scheduler.model.ScheduledOperation;
import by.itacademy.account.scheduler.repository.api.IScheduledOperationRepository;
import by.itacademy.account.scheduler.repository.entity.ScheduledOperationEntity;
import by.itacademy.account.scheduler.service.api.IScheduledOperationService;
import by.itacademy.account.scheduler.service.api.ISchedulerService;
import by.itacademy.account.scheduler.service.api.ValidationError;
import by.itacademy.account.scheduler.service.api.ValidationException;
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

    public ScheduledOperationService(IScheduledOperationRepository scheduledOperationRepository,
                                     ConversionService conversionService,
                                     ISchedulerService schedulerService) {
        this.scheduledOperationRepository = scheduledOperationRepository;
        this.conversionService = conversionService;
        this.schedulerService = schedulerService;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    @Override
    public ScheduledOperation add(ScheduledOperation scheduledOperation) {
        if (scheduledOperation == null) {
            throw new ValidationException("Не передан объект scheduledOperation");
        }

        List<ValidationError> errors = new ArrayList<>();
        Operation operation = scheduledOperation.getOperation();
        Schedule schedule = scheduledOperation.getSchedule();

        this.checkOperation(operation, errors);
        this.checkSchedule(schedule, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Переданы некорректные параметры", errors);
        }

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        scheduledOperation.setId(id);
        scheduledOperation.setDtCreate(now);
        scheduledOperation.setDtUpdate(now);

        try {
            this.scheduledOperationRepository.save(
                    this.conversionService.convert(scheduledOperation, ScheduledOperationEntity.class));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        this.schedulerService.addScheduledOperation(schedule, id);

        return this.get(id);
    }

    @Override
    public ScheduledOperation get(UUID id) {
        List<ValidationError> errors = new ArrayList<>();
        ScheduledOperationEntity entity;

        this.checkIdScheduledOperation(id, errors);

        if (!errors.isEmpty()) {
            throw new ValidationException("Переданы некорректные параметры", errors);
        }

        try {
            entity = this.scheduledOperationRepository.findById(id).get();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        return this.conversionService.convert(entity, ScheduledOperation.class);
    }

    @Override
    public Page<ScheduledOperation> get(Pageable pageable) {
        Page<ScheduledOperationEntity> entities;

        try {
            entities = this.scheduledOperationRepository.findByOrderByDtCreateAsc(pageable);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> this.conversionService.convert(entity, ScheduledOperation.class))
                .collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public ScheduledOperation update(ScheduledOperation scheduledOperation, UUID id, LocalDateTime dtUpdate) {
        if (scheduledOperation == null) {
            throw new ValidationException("Не передан объект scheduledOperation");
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
                entity = this.scheduledOperationRepository.findById(id).orElse(null);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка выполнения SQL", e);
            }
        }

        if (dtUpdate == null) {
            errors.add(new ValidationError("dtUpdate",
                    "Не передан параметр параметр последнего обновления"));
        } else if (entity != null && dtUpdate.compareTo(entity.getDtUpdate()) != 0) {
            errors.add(new ValidationError("dtUpdate",
                    "Передан неверный параметр параметр последнего обновления"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Переданы некорректные параметры", errors);
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

        try {
            this.scheduledOperationRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        this.schedulerService.deleteScheduledOperation(id);
        this.schedulerService.addScheduledOperation(schedule, id);

        return this.get(id);
    }

    private void checkIdScheduledOperation(UUID idScheduledOperation, List<ValidationError> errors) {
        if (idScheduledOperation == null) {
            errors.add(new ValidationError("idScheduledOperation", "Не передан id операции"));
            return;
        }

        try {
            if (this.scheduledOperationRepository.findById(idScheduledOperation).isEmpty()) {
                errors.add(new ValidationError("idScheduledOperation", "Передан id несуществующей операции"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }
    }

    private void checkSchedule(Schedule schedule, List<ValidationError> errors) {
        if (schedule == null) {
            errors.add(new ValidationError("schedule", "Не передан объект schedule"));
            return;
        }

        if (schedule.getInterval() < 0) {
            errors.add(new ValidationError("interval", "Интервал должен быть положительным"));
        } else if (schedule.getInterval() > 0 && schedule.getTimeUnit() == null) {
            errors.add(new ValidationError("timeUnit", "Не передан timeUnit"));
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
            errors.add(new ValidationError("operation", "Не передан объект operation"));
            return;
        }

        String currencyClassifierUrl = this.currencyUrl + "/" + operation.getCurrency();
        String categoryClassifierUrl = this.categoryUrl + "/" + operation.getCategory();
        String accountServiceUrl = this.accountUrl + "/" + operation.getAccount();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        if (operation.getAccount() == null) {
            errors.add(new ValidationError("account", "Не передан счёт (id счёта)"));
        } else {
            try {
                this.restTemplate.exchange(accountServiceUrl, HttpMethod.GET, entity, String.class);
            } catch (HttpStatusCodeException e) {
                errors.add(new ValidationError("account", "Передан id несуществующего счёта"));
            }
        }

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
}
