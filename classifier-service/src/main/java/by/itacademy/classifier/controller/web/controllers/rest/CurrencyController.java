package by.itacademy.classifier.controller.web.controllers.rest;

import by.itacademy.classifier.model.Currency;
import by.itacademy.classifier.service.api.IClassifierService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = {"/classifier/currency", "/classifier/currency/"},
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class CurrencyController {

    private final IClassifierService<Currency, UUID> currencyService;

    public CurrencyController(IClassifierService<Currency, UUID> currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Currency currency) {
        this.currencyService.create(currency);
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Currency> index(@RequestParam int page,
                                @RequestParam int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return this.currencyService.get(pageable);
    }
}
