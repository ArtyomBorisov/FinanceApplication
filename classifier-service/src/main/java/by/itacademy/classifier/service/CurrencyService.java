package by.itacademy.classifier.service;

import by.itacademy.classifier.dto.Currency;
import by.itacademy.classifier.repository.api.ICurrencyRepository;
import by.itacademy.classifier.repository.entity.CurrencyEntity;
import by.itacademy.classifier.service.api.IClassifierService;
import by.itacademy.classifier.exception.MessageError;
import by.itacademy.classifier.exception.ValidationError;
import by.itacademy.classifier.exception.ValidationException;
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
        checkCurrency(currency);

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        currency.setId(id);
        currency.setDtCreate(now);
        currency.setDtUpdate(now);

        CurrencyEntity saveEntity;

        try {
            saveEntity = currencyRepository.save(
                    conversionService.convert(currency, CurrencyEntity.class));
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return conversionService.convert(saveEntity, Currency.class);
    }

    @Override
    public Page<Currency> get(Pageable pageable) {
        Page<CurrencyEntity> entities;

        try {
            entities = currencyRepository.findByOrderByTitle(pageable);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> conversionService.convert(entity, Currency.class))
                .collect(Collectors.toList()), pageable, entities.getTotalElements());
    }

    @Override
    public Currency get(UUID uuid) {
        CurrencyEntity entity;

        try {
            entity = currencyRepository.findById(uuid).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        if (entity == null) {
            throw new ValidationException(MessageError.ID_NOT_EXIST);
        }

        return conversionService.convert(entity, Currency.class);
    }

    @Override
    public Page<Currency> get(Collection<UUID> collectionId, Pageable pageable) {
        Page<CurrencyEntity> entities;

        if (collectionId == null || collectionId.isEmpty()) {
            entities = currencyRepository.findAll(pageable);
        } else {
            checkCollectionIdCurrency(collectionId);

            entities = currencyRepository.findByIdInOrderByTitle(collectionId, pageable);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> conversionService.convert(entity, Currency.class))
                .collect(Collectors.toList()), pageable, entities.getTotalElements());
    }

    private void checkCurrency(Currency currency) {
        if (currency == null) {
            throw new ValidationException(new ValidationError("currency", MessageError.MISSING_OBJECT));
        }

        List<ValidationError> errors = new ArrayList<>();

        if (nullOrEmpty(currency.getTitle())) {
            errors.add(new ValidationError("title (код валюты)", MessageError.MISSING_FIELD));
        } else if (currencyRepository.findByTitle(currency.getTitle()).isPresent()) {
            errors.add(new ValidationError("title (код валюты)", MessageError.NO_UNIQUE_FIELD));
        }

        if (this.nullOrEmpty(currency.getDescription())) {
            errors.add(new ValidationError("description (описание валюты)", MessageError.MISSING_FIELD));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void checkCollectionIdCurrency(Collection<UUID> collection) {
        List<ValidationError> errors = new ArrayList<>();

        for (UUID id : collection) {
            if (!currencyRepository.existsCategoryEntityById(id)) {
                errors.add(new ValidationError(id.toString(), MessageError.ID_NOT_EXIST));
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private boolean nullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
