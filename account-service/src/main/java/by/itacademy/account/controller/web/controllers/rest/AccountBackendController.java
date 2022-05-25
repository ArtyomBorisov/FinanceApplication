package by.itacademy.account.controller.web.controllers.rest;

import by.itacademy.account.model.Account;
import by.itacademy.account.service.api.IAccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping(value = {"/backend/account", "/backend/account/"},
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class AccountBackendController {

    private final IAccountService accountService;

    public AccountBackendController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Account> index(@RequestBody Collection<UUID> uuids,
                               @RequestParam int page,
                               @RequestParam int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return this.accountService.getInOrderByTitle(uuids, pageable);
    }
}
