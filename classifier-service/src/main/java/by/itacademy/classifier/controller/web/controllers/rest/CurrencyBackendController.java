package by.itacademy.classifier.controller.web.controllers.rest;

import by.itacademy.classifier.model.Currency;
import by.itacademy.classifier.service.api.IClassifierService;
import by.itacademy.classifier.service.api.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = {"/backend/classifier/currency", "/backend/classifier/currency/"},
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class CurrencyBackendController {

    private final IClassifierService<Currency, UUID> currencyService;

    public CurrencyBackendController(IClassifierService<Currency, UUID> currencyService) {
        this.currencyService = currencyService;
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Currency> index(@RequestBody Collection<String> currenciesUuid,
                                @RequestParam int page,
                                @RequestParam int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        try {
            List<UUID> uuidList = currenciesUuid.stream().map(UUID::fromString).collect(Collectors.toList());
            return this.currencyService.get(uuidList, pageable);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Передан неверный(ые) uuid");
        }
    }

    @GetMapping(value = {"/{uuid}/", "/{uuid}"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Currency index(@PathVariable(name = "uuid") UUID id) {
        return this.currencyService.get(id);
    }
}
