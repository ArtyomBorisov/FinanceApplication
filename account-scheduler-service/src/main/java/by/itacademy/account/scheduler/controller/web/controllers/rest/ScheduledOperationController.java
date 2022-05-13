package by.itacademy.account.scheduler.controller.web.controllers.rest;

import by.itacademy.account.scheduler.model.ScheduledOperation;
import by.itacademy.account.scheduler.service.api.IScheduledOperationService;
import by.itacademy.account.scheduler.service.api.ValidationException;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping(value = {"/scheduler/operation", "/scheduler/operation/"},
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class ScheduledOperationController {

    private final IScheduledOperationService scheduledOperationService;
    private final ConversionService conversionService;

    public ScheduledOperationController(IScheduledOperationService scheduledOperationService,
                                        ConversionService conversionService) {
        this.scheduledOperationService = scheduledOperationService;
        this.conversionService = conversionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody ScheduledOperation scheduledOperation) {
        this.scheduledOperationService.add(scheduledOperation);
    }

    @GetMapping
    @ResponseBody
    public Page<ScheduledOperation> index(@RequestParam int page,
                                          @RequestParam int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return this.scheduledOperationService.get(pageable);
    }

    @PutMapping(value = {"{uuid}/dt_update/{dt_update}", "{uuid}/dt_update/{dt_update}/"})
    public void update(@PathVariable(name = "uuid") UUID id,
                       @PathVariable(name = "dt_update") String dtUpdateString,
                       @RequestBody ScheduledOperation scheduledOperation) {
        long dtUpdateLong;
        try {
            dtUpdateLong = Long.parseLong(dtUpdateString);
        } catch (NumberFormatException e) {
            throw new ValidationException("Передан неверный формат параметра последнего обновления");
        }

        LocalDateTime dtUpdate = this.conversionService.convert(dtUpdateLong, LocalDateTime.class);
        this.scheduledOperationService.update(scheduledOperation, id, dtUpdate);
    }

    
}
