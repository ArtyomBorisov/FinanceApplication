package by.itacademy.account.scheduler.service.impl;

import by.itacademy.account.scheduler.dto.Operation;
import by.itacademy.account.scheduler.dto.Schedule;
import by.itacademy.account.scheduler.dto.ScheduledOperation;
import by.itacademy.account.scheduler.exception.OptimisticLockException;
import by.itacademy.account.scheduler.dao.ScheduledOperationRepository;
import by.itacademy.account.scheduler.dao.entity.ScheduledOperationEntity;
import by.itacademy.account.scheduler.service.ScheduledOperationService;
import by.itacademy.account.scheduler.service.SchedulerService;
import by.itacademy.account.scheduler.service.UserHolder;
import by.itacademy.account.scheduler.utils.Generator;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ScheduledOperationServiceImpl implements ScheduledOperationService {

    private final ScheduledOperationRepository scheduledOperationRepository;
    private final ConversionService conversionService;
    private final SchedulerService schedulerService;
    private final UserHolder userHolder;
    private final Generator generator;

    public ScheduledOperationServiceImpl(ScheduledOperationRepository scheduledOperationRepository,
                                         ConversionService conversionService,
                                         SchedulerService schedulerService,
                                         UserHolder userHolder,
                                         Generator generator) {
        this.scheduledOperationRepository = scheduledOperationRepository;
        this.conversionService = conversionService;
        this.schedulerService = schedulerService;
        this.userHolder = userHolder;
        this.generator = generator;
    }

    @Transactional
    @Override
    public ScheduledOperation add(ScheduledOperation scheduledOperation) {
        UUID id = generateIdAndTimeAndAddToOperation(scheduledOperation);
        Operation operation = scheduledOperation.getOperation();
        Schedule schedule = scheduledOperation.getSchedule();
        operation.setUser(userHolder.getLoginFromContext());
        ScheduledOperationEntity entityForSaving = conversionService.convert(scheduledOperation, ScheduledOperationEntity.class);
        ScheduledOperationEntity savedOperation = scheduledOperationRepository.save(entityForSaving);
        schedulerService.addScheduledOperation(schedule, id);
        return conversionService.convert(savedOperation, ScheduledOperation.class);
    }

    @Override
    public ScheduledOperation get(UUID id) {
        String login = userHolder.getLoginFromContext();
        return scheduledOperationRepository.findByUserAndId(login, id)
                .map(entity -> conversionService.convert(entity, ScheduledOperation.class))
                .orElse(null);
    }

    @Override
    public Page<ScheduledOperation> get(Pageable pageable) {
        String login = userHolder.getLoginFromContext();
        Page<ScheduledOperationEntity> entities =
                scheduledOperationRepository.findByUserOrderByDtCreateAsc(login, pageable);
        return convertToDtoPage(entities);
    }

    @Transactional
    @Override
    public ScheduledOperation update(ScheduledOperation scheduledOperation, UUID id, LocalDateTime dtUpdate) {
        String login = userHolder.getLoginFromContext();
        ScheduledOperationEntity entity = scheduledOperationRepository.findByUserAndId(login, id).orElse(null);

        if (entity == null) {
            return null;
        }
        if (dtUpdate.compareTo(entity.getDtUpdate()) != 0) {
            throw new OptimisticLockException();
        }

        updateEntity(entity, scheduledOperation);
        ScheduledOperationEntity updatedOperation = scheduledOperationRepository.save(entity);
        schedulerService.deleteScheduledOperation(id);
        Schedule schedule = scheduledOperation.getSchedule();
        schedulerService.addScheduledOperation(schedule, id);
        return conversionService.convert(updatedOperation, ScheduledOperation.class);
    }

    private UUID generateIdAndTimeAndAddToOperation(ScheduledOperation scheduledOperation) {
        UUID id = generator.generateUUID();
        LocalDateTime now = generator.now();
        scheduledOperation.setId(id);
        scheduledOperation.setDtCreate(now);
        scheduledOperation.setDtUpdate(now);
        return id;
    }

    private Page<ScheduledOperation> convertToDtoPage(Page<ScheduledOperationEntity> entities) {
        return entities.map(entity -> conversionService.convert(entity, ScheduledOperation.class));
    }

    private void updateEntity(ScheduledOperationEntity entity, ScheduledOperation dto) {
        Operation operation = dto.getOperation();
        Schedule schedule = dto.getSchedule();

        entity.setStartTime(schedule.getStartTime());
        entity.setStopTime(schedule.getStopTime());
        entity.setInterval(schedule.getInterval());
        entity.setTimeUnit(schedule.getTimeUnit() != null ? schedule.getTimeUnit().toString() : null);
        entity.setAccount(operation.getAccount());
        entity.setDescription(operation.getDescription());
        entity.setValue(operation.getValue());
        entity.setCurrency(operation.getCurrency());
        entity.setCategory(operation.getCategory());
    }
}
