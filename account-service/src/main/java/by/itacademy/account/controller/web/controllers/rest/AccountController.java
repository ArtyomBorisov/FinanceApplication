package by.itacademy.account.controller.web.controllers.rest;

import by.itacademy.account.model.Account;
import by.itacademy.account.service.api.IAccountService;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping(value = {"/account", "/account/"},
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class AccountController {

    private final IAccountService accountService;
    private final ConversionService conversionService;

    public AccountController(IAccountService accountService,
                             ConversionService conversionService) {
        this.accountService = accountService;
        this.conversionService = conversionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Account account) {
        this.accountService.add(account);
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Account> index(@RequestParam int page,
                               @RequestParam int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return this.accountService.get(pageable);
    }

    @GetMapping(value = {"/{uuid}", "/{uuid}/"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Account index(@PathVariable(name = "uuid") UUID id) {
        return this.accountService.get(id);
    }

    @PutMapping(value = {"/{uuid}/dt_update/{dt_update}", "/{uuid}/dt_update/{dt_update}/"})
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable(name = "uuid") UUID id,
                       @PathVariable(name = "dt_update") LocalDateTime dtUpdate,
                       @RequestBody Account account) {
        this.accountService.update(account, id, dtUpdate);
    }
}
