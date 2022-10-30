package by.itacademy.account.controller.web.controllers.rest;

import by.itacademy.account.dto.Account;
import by.itacademy.account.service.api.IAccountService;
import by.itacademy.account.exception.MessageError;
import by.itacademy.account.exception.ValidationError;
import by.itacademy.account.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/backend/account")
@Validated
public class AccountBackendController {

    private final IAccountService accountService;

    public AccountBackendController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
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

        return accountService.get(uuidList, pageable);
    }
}
