package by.itacademy.account.scheduler.converter;

import by.itacademy.account.scheduler.dto.Operation;
import by.itacademy.account.scheduler.dto.Schedule;
import by.itacademy.account.scheduler.dto.ScheduledOperation;
import by.itacademy.account.scheduler.dao.entity.ScheduledOperationEntity;
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
            entity.setUser(operation.getUser());
        }

        return entity;
    }
}
