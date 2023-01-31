package by.itacademy.classifier.controller.rest;

import by.itacademy.classifier.constant.MessageError;
import by.itacademy.classifier.dto.Currency;
import by.itacademy.classifier.service.ClassifierService;
import by.itacademy.classifier.validation.annotation.CurrencyValid;
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

    private final ClassifierService<Currency, UUID> currencyService;

    public CurrencyController(ClassifierService<Currency, UUID> currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @CurrencyValid Currency currency) {
        currencyService.create(currency);
    }

    @GetMapping
    public Page<Currency> index(@RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                                @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return currencyService.get(pageable);
    }
}
