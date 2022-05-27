package by.itacademy.account.controller.web.controllers.rest;

import by.itacademy.account.model.Operation;
import by.itacademy.account.service.api.IOperationService;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping(value = {"/account/{uuid}/operation", "/account/{uuid}/operation/"},
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class OperationController {

    private final IOperationService operationService;

    public OperationController(IOperationService operationService) {
        this.operationService = operationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable(name = "uuid") UUID idAccount,
                       @RequestBody Operation operation) {
        this.operationService.add(idAccount, operation);
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Operation> index(@PathVariable(name = "uuid") UUID idAccount,
                                 @RequestParam int page,
                                 @RequestParam int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return this.operationService.get(idAccount, pageable);
    }

    @PutMapping(value = {"/{uuid_operation}/dt_update/{dt_update}", "/{uuid_operation}/dt_update/{dt_update}/"})
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable(name = "uuid") UUID idAccount,
                       @PathVariable(name = "uuid_operation") UUID idOperation,
                       @PathVariable(name = "dt_update") LocalDateTime dtUpdate,
                       @RequestBody Operation operation) {
        this.operationService.update(operation, idAccount, idOperation, dtUpdate);
    }

    @DeleteMapping(value = {"/{uuid_operation}/dt_update/{dt_update}", "/{uuid_operation}/dt_update/{dt_update}/"})
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable(name = "uuid") UUID idAccount,
                       @PathVariable(name = "uuid_operation") UUID idOperation,
                       @PathVariable(name = "dt_update") LocalDateTime dtUpdate) {
        this.operationService.delete(idAccount, idOperation, dtUpdate);
    }
}
