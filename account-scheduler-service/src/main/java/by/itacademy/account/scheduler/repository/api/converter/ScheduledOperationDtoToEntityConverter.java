package by.itacademy.account.scheduler.repository.api.converter;

import by.itacademy.account.scheduler.model.Operation;
import by.itacademy.account.scheduler.model.Schedule;
import by.itacademy.account.scheduler.model.ScheduledOperation;
import by.itacademy.account.scheduler.repository.entity.ScheduledOperationEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ScheduledOperationDtoToEntityConverter implements Converter<ScheduledOperation, ScheduledOperationEntity> {
    @Override
    public ScheduledOperationEntity convert(ScheduledOperation dto) {
        ScheduledOperationEntity entity = ScheduledOperationEntity.Builder.createBuilder()
                .setId(dto.getId())
                .setDtCreate(dto.getDtCreate())
                .setDtUpdate(dto.getDtUpdate())
                .build();

        Schedule schedule = dto.getSchedule();
        if (schedule != null) {
            entity.setStartTime(schedule.getStartTime());
            entity.setStopTime(schedule.getStopTime());
            entity.setInterval(schedule.getInterval());
            entity.setTimeUnit(schedule.getTimeUnit() == null
                    ? null : schedule.getTimeUnit().toString());
        }

        Operation operation = dto.getOperation();
        if (operation != null) {
            entity.setAccount(operation.getAccount());
            entity.setDescription(operation.getDescription());
            entity.setValue(operation.getValue());
            entity.setCurrency(operation.getCurrency());
            entity.setCategory(operation.getCategory());
        }

        return entity;
    }
}
