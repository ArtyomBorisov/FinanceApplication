package by.itacademy.account.controller.rest;

import by.itacademy.account.dto.Operation;
import by.itacademy.account.dto.Params;
import by.itacademy.account.service.OperationService;
import by.itacademy.account.constant.MessageError;
import by.itacademy.account.validation.annotation.CustomValid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@RestController
@RequestMapping
@Validated
public class OperationBackendController {

    private final OperationService operationService;

    public OperationBackendController(OperationService operationService) {
        this.operationService = operationService;
    }

    @PostMapping("/backend/operation")
    public Page<Operation> get(@RequestBody @CustomValid Params params,
                               @RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                               @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return operationService.getByParams(params, pageable);
    }
}
