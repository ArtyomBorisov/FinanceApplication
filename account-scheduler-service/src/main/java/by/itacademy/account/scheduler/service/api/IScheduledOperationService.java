package by.itacademy.account.scheduler.service.api;

import by.itacademy.account.scheduler.model.ScheduledOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IScheduledOperationService {
    ScheduledOperation add(ScheduledOperation scheduledOperation);
    ScheduledOperation get(UUID id);
    Page<ScheduledOperation> get(Pageable pageable);
    ScheduledOperation update(ScheduledOperation scheduledOperation, UUID id, LocalDateTime dtUpdate);
}
