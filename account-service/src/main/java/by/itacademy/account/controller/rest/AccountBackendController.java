package by.itacademy.account.controller.rest;

import by.itacademy.account.dto.Account;
import by.itacademy.account.service.AccountService;
import by.itacademy.account.constant.MessageError;
import by.itacademy.account.validation.annotation.AccountExist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/backend/account")
@Validated
public class AccountBackendController {

    private final AccountService accountService;

    public AccountBackendController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public Page<Account> get(@RequestBody @AccountExist Collection<UUID> uuids,
                             @RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                             @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return accountService.get(uuids, pageable);
    }
}
