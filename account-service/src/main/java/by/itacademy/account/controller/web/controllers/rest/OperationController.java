package by.itacademy.account.controller.web.controllers.rest;

import by.itacademy.account.dto.Operation;
import by.itacademy.account.service.api.IOperationService;
import by.itacademy.account.exception.MessageError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/account/{uuid}/operation")
@Validated
public class OperationController {

    private final IOperationService operationService;

    public OperationController(IOperationService operationService) {
        this.operationService = operationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable(name = "uuid") UUID idAccount,
                       @RequestBody Operation operation) {
        operationService.add(idAccount, operation);
    }

    @GetMapping
    public Page<Operation> index(@PathVariable(name = "uuid") UUID idAccount,
                                 @RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                                 @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return operationService.get(idAccount, pageable);
    }

    @PutMapping("/{uuid_operation}/dt_update/{dt_update}")
    public void update(@PathVariable(name = "uuid") UUID idAccount,
                       @PathVariable(name = "uuid_operation") UUID idOperation,
                       @PathVariable(name = "dt_update") LocalDateTime dtUpdate,
                       @RequestBody Operation operation) {
        operationService.update(operation, idAccount, idOperation, dtUpdate);
    }

    @DeleteMapping("/{uuid_operation}/dt_update/{dt_update}")
    public void delete(@PathVariable(name = "uuid") UUID idAccount,
                       @PathVariable(name = "uuid_operation") UUID idOperation,
                       @PathVariable(name = "dt_update") LocalDateTime dtUpdate) {
        operationService.delete(idAccount, idOperation, dtUpdate);
    }
}
