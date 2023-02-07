package by.itacademy.account.controller.rest;

import by.itacademy.account.dto.Operation;
import by.itacademy.account.service.OperationService;
import by.itacademy.account.constant.MessageError;
import by.itacademy.account.validation.annotation.AccountExist;
import by.itacademy.account.validation.annotation.CustomValid;
import by.itacademy.account.validation.annotation.OperationExist;
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

    private final OperationService operationService;

    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable(name = "uuid") @AccountExist UUID idAccount,
                       @RequestBody @CustomValid Operation operation) {
        operationService.add(idAccount, operation);
    }

    @GetMapping
    public Page<Operation> get(@PathVariable(name = "uuid") @AccountExist UUID idAccount,
                               @RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                               @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return operationService.get(idAccount, pageable);
    }

    @PutMapping("/{uuid_operation}/dt_update/{dt_update}")
    public void update(@PathVariable(name = "uuid") UUID idAccount,
                       @PathVariable(name = "uuid_operation") @OperationExist UUID idOperation,
                       @PathVariable(name = "dt_update") LocalDateTime dtUpdate,
                       @RequestBody @CustomValid Operation operation) {
        operationService.update(operation, idAccount, idOperation, dtUpdate);
    }

    @DeleteMapping("/{uuid_operation}/dt_update/{dt_update}")
    public void delete(@PathVariable(name = "uuid") UUID idAccount,
                       @PathVariable(name = "uuid_operation") @OperationExist UUID idOperation,
                       @PathVariable(name = "dt_update") LocalDateTime dtUpdate) {
        operationService.delete(idAccount, idOperation, dtUpdate);
    }
}
