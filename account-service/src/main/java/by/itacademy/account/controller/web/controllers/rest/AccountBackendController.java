package by.itacademy.account.controller.web.controllers.rest;

import by.itacademy.account.model.Account;
import by.itacademy.account.service.api.IAccountService;
import by.itacademy.account.service.api.MessageError;
import by.itacademy.account.service.api.ValidationError;
import by.itacademy.account.service.api.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/backend/account", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class AccountBackendController {

    private final IAccountService accountService;

    public AccountBackendController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Account> index(@RequestBody Collection<String> uuids,
                               @RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                               @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        List<UUID> uuidList;

        try {
            uuidList = uuids.stream().map(UUID::fromString).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(new ValidationError("uuid", MessageError.INVALID_FORMAT));
        }

        return this.accountService.get(uuidList, pageable);
    }
}
