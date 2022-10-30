package by.itacademy.account.scheduler.controller.web.controllers.rest;

import by.itacademy.account.scheduler.dto.ScheduledOperation;
import by.itacademy.account.scheduler.service.api.IScheduledOperationService;
import by.itacademy.account.scheduler.exception.MessageError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/scheduler/operation")
public class ScheduledOperationController {

    private final IScheduledOperationService scheduledOperationService;

    public ScheduledOperationController(IScheduledOperationService scheduledOperationService) {
        this.scheduledOperationService = scheduledOperationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody ScheduledOperation scheduledOperation) {
        scheduledOperationService.add(scheduledOperation);
    }

    @GetMapping
    public Page<ScheduledOperation> index(@RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                                          @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return scheduledOperationService.get(pageable);
    }

    @PutMapping("{uuid}/dt_update/{dt_update}")
    public void update(@PathVariable(name = "uuid") UUID id,
                       @PathVariable(name = "dt_update") LocalDateTime dtUpdate,
                       @RequestBody ScheduledOperation scheduledOperation) {
        scheduledOperationService.update(scheduledOperation, id, dtUpdate);
    }

    
}
