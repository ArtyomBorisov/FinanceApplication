package by.itacademy.account.controller.web.controllers.rest;

import by.itacademy.account.model.Operation;
import by.itacademy.account.service.api.IOperationService;
import by.itacademy.account.service.api.MessageError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class OperationBackendController {

    private final IOperationService operationService;

    public OperationBackendController(IOperationService operationService) {
        this.operationService = operationService;
    }

    @PostMapping(value = "/backend/account/{uuid}/operation")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable(name = "uuid") UUID idAccount,
                       @RequestBody Operation operation) {
        this.operationService.add(idAccount, operation);
    }

    @PostMapping(value = "/backend/operation", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Operation> get(@RequestBody Map<String, Object> params,
                               @RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                               @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return this.operationService.getByParams(params, pageable);
    }
}
