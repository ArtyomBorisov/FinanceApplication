package by.itacademy.classifier.controller.web.controllers.rest;

import by.itacademy.classifier.dto.Currency;
import by.itacademy.classifier.service.api.IClassifierService;
import by.itacademy.classifier.exception.MessageError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.UUID;

@RestController
@RequestMapping("/classifier/currency")
@Validated
public class CurrencyController {

    private final IClassifierService<Currency, UUID> currencyService;

    public CurrencyController(IClassifierService<Currency, UUID> currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Currency currency) {
        currencyService.create(currency);
    }

    @GetMapping
    public Page<Currency> index(@RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                                @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return currencyService.get(pageable);
    }
}
