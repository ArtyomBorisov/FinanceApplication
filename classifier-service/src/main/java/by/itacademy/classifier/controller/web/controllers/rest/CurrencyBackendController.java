package by.itacademy.classifier.controller.web.controllers.rest;

import by.itacademy.classifier.model.Currency;
import by.itacademy.classifier.service.api.IClassifierService;
import by.itacademy.classifier.service.api.MessageError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping(value = "/backend/classifier/currency", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class CurrencyBackendController {

    private final IClassifierService<Currency, UUID> currencyService;

    public CurrencyBackendController(IClassifierService<Currency, UUID> currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Currency> index(@RequestBody Collection<UUID> currencies,
                                @RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                                @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return this.currencyService.get(currencies, pageable);
    }

    @GetMapping(value = "/{uuid}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Currency index(@PathVariable(name = "uuid") UUID id) {
        return this.currencyService.get(id);
    }
}
