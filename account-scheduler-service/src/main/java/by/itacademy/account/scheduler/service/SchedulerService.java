package by.itacademy.account.scheduler.service;

import by.itacademy.account.scheduler.dto.Schedule;

import java.util.UUID;

public interface SchedulerService {
    void addScheduledOperation(Schedule schedule, UUID idScheduledOperation);
    void deleteScheduledOperation(UUID idScheduledOperation);
}
