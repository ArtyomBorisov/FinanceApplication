package by.itacademy.account.controller.web.controllers.rest;

import by.itacademy.account.dto.Account;
import by.itacademy.account.service.api.IAccountService;
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
@RequestMapping("/account")
@Validated
public class AccountController {

    private final IAccountService accountService;

    public AccountController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Account account) {
        accountService.add(account);
    }

    @GetMapping
    public Page<Account> index(@RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                               @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return accountService.get(pageable);
    }

    @GetMapping("/{uuid}")
    public Account index(@PathVariable(name = "uuid") UUID id) {
        return accountService.get(id);
    }

    @PutMapping("/{uuid}/dt_update/{dt_update}")
    public void update(@PathVariable(name = "uuid") UUID id,
                       @PathVariable(name = "dt_update") LocalDateTime dtUpdate,
                       @RequestBody Account account) {
        accountService.update(account, id, dtUpdate);
    }
}
