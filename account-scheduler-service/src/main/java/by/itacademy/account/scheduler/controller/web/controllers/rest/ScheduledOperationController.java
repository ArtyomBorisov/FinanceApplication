package by.itacademy.account.scheduler.controller.web.controllers.rest;

import by.itacademy.account.scheduler.model.ScheduledOperation;
import by.itacademy.account.scheduler.service.api.IScheduledOperationService;
import by.itacademy.account.scheduler.service.api.MessageError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping(value = "/scheduler/operation")
public class ScheduledOperationController {

    private final IScheduledOperationService scheduledOperationService;

    public ScheduledOperationController(IScheduledOperationService scheduledOperationService) {
        this.scheduledOperationService = scheduledOperationService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody ScheduledOperation scheduledOperation) {
        this.scheduledOperationService.add(scheduledOperation);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<ScheduledOperation> index(@RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                                          @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return this.scheduledOperationService.get(pageable);
    }

    @PutMapping(value = "{uuid}/dt_update/{dt_update}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable(name = "uuid") UUID id,
                       @PathVariable(name = "dt_update") LocalDateTime dtUpdate,
                       @RequestBody ScheduledOperation scheduledOperation) {
        this.scheduledOperationService.update(scheduledOperation, id, dtUpdate);
    }

    
}
