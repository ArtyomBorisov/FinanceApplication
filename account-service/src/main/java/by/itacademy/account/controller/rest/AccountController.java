package by.itacademy.account.controller.rest;

import by.itacademy.account.dto.Account;
import by.itacademy.account.service.AccountService;
import by.itacademy.account.constant.MessageError;
import by.itacademy.account.validation.annotation.CustomValid;
import by.itacademy.account.validation.annotation.AccountUpdatingValid;
import by.itacademy.account.validation.annotation.AccountExist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/account")
@Validated
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @CustomValid Account account) {
        accountService.add(account);
    }

    @GetMapping
    public Page<Account> get(@RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                             @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return accountService.get(pageable);
    }

    @GetMapping("/{uuid}")
    public Account get(@PathVariable(name = "uuid") @AccountExist UUID id) {
        return accountService.get(id);
    }

    @PutMapping("/{uuid}/dt_update/{dt_update}")
    public void update(@PathVariable(name = "uuid") UUID id,
                       @PathVariable(name = "dt_update") LocalDateTime dtUpdate,
                       @RequestBody @AccountUpdatingValid Account account) {
        accountService.update(account, id, dtUpdate);
    }
}
