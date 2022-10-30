package by.itacademy.account.scheduler.service.api;

import by.itacademy.account.scheduler.dto.Schedule;

import java.util.UUID;

public interface ISchedulerService {
    void addScheduledOperation(Schedule schedule, UUID idScheduledOperation);
    void deleteScheduledOperation(UUID idScheduledOperation);
}
