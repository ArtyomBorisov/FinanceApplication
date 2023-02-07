package by.itacademy.account.scheduler.utils.converter;

import by.itacademy.account.scheduler.dto.Operation;
import by.itacademy.account.scheduler.dto.Schedule;
import by.itacademy.account.scheduler.dto.ScheduledOperation;
import by.itacademy.account.scheduler.constant.TimeUnit;
import by.itacademy.account.scheduler.repository.entity.ScheduledOperationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ScheduledOperationEntityToDtoConverter implements Converter<ScheduledOperationEntity, ScheduledOperation> {
    @Override
    public ScheduledOperation convert(ScheduledOperationEntity entity) {
        return ScheduledOperation.Builder.createBuilder()
                .setId(entity.getId())
                .setDtCreate(entity.getDtCreate())
                .setDtUpdate(entity.getDtUpdate())
                .setSchedule(Schedule.Builder.createBuilder()
                        .setStartTime(entity.getStartTime())
                        .setStopTime(entity.getStopTime())
                        .setInterval(entity.getInterval())
                        .setTimeUnit(TimeUnit.valueOf(entity.getTimeUnit()))
                        .build())
                .setOperation(Operation.Builder.createBuilder()
                        .setAccount(entity.getAccount())
                        .setDescription(entity.getDescription())
                        .setValue(entity.getValue())
                        .setCurrency(entity.getCurrency())
                        .setCategory(entity.getCategory())
                        .setUser(entity.getUser())
                        .build())
                .build();
    }
}
