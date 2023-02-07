package by.itacademy.account.scheduler.controller.rest;

import by.itacademy.account.scheduler.dto.ScheduledOperation;
import by.itacademy.account.scheduler.service.ScheduledOperationService;
import by.itacademy.account.scheduler.constant.MessageError;
import by.itacademy.account.scheduler.validation.annotation.CustomValid;
import by.itacademy.account.scheduler.validation.annotation.Exist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/scheduler/operation")
@Validated
public class ScheduledOperationController {

    private final ScheduledOperationService scheduledOperationService;

    public ScheduledOperationController(ScheduledOperationService scheduledOperationService) {
        this.scheduledOperationService = scheduledOperationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @CustomValid ScheduledOperation scheduledOperation) {
        scheduledOperationService.add(scheduledOperation);
    }

    @GetMapping
    public Page<ScheduledOperation> get(@RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                                        @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return scheduledOperationService.get(pageable);
    }

    @PutMapping("{uuid}/dt_update/{dt_update}")
    public void update(@PathVariable(name = "uuid") @Exist UUID id,
                       @PathVariable(name = "dt_update") LocalDateTime dtUpdate,
                       @RequestBody @CustomValid ScheduledOperation scheduledOperation) {
        scheduledOperationService.update(scheduledOperation, id, dtUpdate);
    }

    
}
