package by.itacademy.account.controller.web.controllers.rest;

import by.itacademy.account.model.Operation;
import by.itacademy.account.service.api.IOperationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = {"/backend/operation", "/backend/operation/"},
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class OperationBackendController {

    private final IOperationService operationService;

    public OperationBackendController(IOperationService operationService) {
        this.operationService = operationService;
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Operation> get(@RequestBody Map<String, Object> params,
                               @RequestParam int page,
                               @RequestParam int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return this.operationService.getByParams(params, pageable);
    }


}
