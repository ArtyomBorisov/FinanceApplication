package by.itacademy.classifier.controller.rest;

import by.itacademy.classifier.constant.MessageError;
import by.itacademy.classifier.dto.Currency;
import by.itacademy.classifier.service.ClassifierService;
import by.itacademy.classifier.validation.annotation.CurrencyExist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/backend/classifier/currency")
@Validated
public class CurrencyBackendController {

    private final ClassifierService<Currency, UUID> currencyService;

    public CurrencyBackendController(ClassifierService<Currency, UUID> currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping
    public Page<Currency> get(@RequestBody @CurrencyExist Collection<UUID> currencies,
                              @RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                              @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return currencyService.get(currencies, pageable);
    }

    @GetMapping("/{uuid}")
    public Currency get(@PathVariable(name = "uuid") @CurrencyExist UUID id) {
        return currencyService.get(id);
    }
}
