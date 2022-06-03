package by.itacademy.classifier.controller.web.controllers.rest;

import by.itacademy.classifier.model.Currency;
import by.itacademy.classifier.service.api.IClassifierService;
import by.itacademy.classifier.service.api.MessageError;
import by.itacademy.classifier.service.api.ValidationError;
import by.itacademy.classifier.service.api.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public Page<Currency> index(@RequestBody Collection<String> currenciesUuid,
                                @RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                                @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        List<UUID> uuidList;

        try {
            uuidList = currenciesUuid.stream().map(UUID::fromString).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(new ValidationError("uuid", MessageError.INVALID_FORMAT));
        }

        return this.currencyService.get(uuidList, pageable);
    }

    @GetMapping(value = "/{uuid}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Currency index(@PathVariable(name = "uuid") UUID id) {
        return this.currencyService.get(id);
    }
}
