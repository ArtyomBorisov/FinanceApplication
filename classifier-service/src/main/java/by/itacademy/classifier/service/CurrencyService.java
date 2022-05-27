package by.itacademy.classifier.service;

import by.itacademy.classifier.model.Currency;
import by.itacademy.classifier.repository.api.ICurrencyRepository;
import by.itacademy.classifier.repository.entity.CurrencyEntity;
import by.itacademy.classifier.service.api.IClassifierService;
import by.itacademy.classifier.service.api.ValidationError;
import by.itacademy.classifier.service.api.ValidationException;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CurrencyService implements IClassifierService<Currency, UUID> {

    private final ICurrencyRepository currencyRepository;
    private final ConversionService conversionService;

    public CurrencyService(ICurrencyRepository currencyRepository,
                           ConversionService conversionService) {
        this.currencyRepository = currencyRepository;
        this.conversionService = conversionService;
    }

    @Transactional
    @Override
    public Currency create(Currency currency) {
        if (currency == null) {
            throw new ValidationException("Не передан объект currency");
        }

        List<ValidationError> errors = new ArrayList<>();

        if (this.nullOrEmpty(currency.getTitle())) {
            errors.add(new ValidationError("title", "Не передан код валюты (или передан пустой)"));
        } else if (this.currencyRepository.findByTitle(currency.getTitle()).isPresent()) {
            errors.add(new ValidationError("title", "Передан не уникальный код валюты"));
        }

        if (this.nullOrEmpty(currency.getDescription())) {
            errors.add(new ValidationError("description",
                    "Не передано описание валюты (или передано пустой)"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Переданы некорректные параметры", errors);
        }

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        currency.setId(id);
        currency.setDtCreate(now);
        currency.setDtUpdate(now);

        CurrencyEntity saveEntity;

        try {
            saveEntity = this.currencyRepository.save(
                    this.conversionService.convert(currency, CurrencyEntity.class));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        return this.conversionService.convert(saveEntity, Currency.class);
    }

    @Override
    public Page<Currency> get(Pageable pageable) {
        Page<CurrencyEntity> entities;

        try {
            entities = this.currencyRepository.findAll(pageable);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> this.conversionService.convert(entity, Currency.class))
                .collect(Collectors.toList()));
    }

    @Override
    public Currency get(UUID uuid) {
        CurrencyEntity entity;

        try {
            entity = this.currencyRepository.findById(uuid).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        if (entity == null) {
            throw new ValidationException("Валюта с id " + uuid + " не найдена");
        }

        return this.conversionService.convert(entity, Currency.class);
    }

    @Override
    public Page<Currency> get(Collection<UUID> collectionId, Pageable pageable) {
        Page<CurrencyEntity> entities;

        if (collectionId == null || collectionId.isEmpty()) {
            entities = this.currencyRepository.findAll(pageable);
        } else {
            entities = this.currencyRepository.findByIdInOrderByTitle(collectionId, pageable);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> this.conversionService.convert(entity, Currency.class))
                .collect(Collectors.toList()));
    }

    private boolean nullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
