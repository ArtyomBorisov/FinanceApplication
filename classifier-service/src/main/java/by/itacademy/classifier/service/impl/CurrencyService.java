package by.itacademy.classifier.service.impl;

import by.itacademy.classifier.dto.Currency;
import by.itacademy.classifier.repository.CurrencyRepository;
import by.itacademy.classifier.repository.entity.CurrencyEntity;
import by.itacademy.classifier.service.ClassifierService;
import by.itacademy.classifier.utils.Generator;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CurrencyService implements ClassifierService<Currency, UUID> {

    private final CurrencyRepository currencyRepository;
    private final ConversionService conversionService;
    private final Generator generator;

    public CurrencyService(CurrencyRepository currencyRepository,
                           ConversionService conversionService,
                           Generator generator) {
        this.currencyRepository = currencyRepository;
        this.conversionService = conversionService;
        this.generator = generator;
    }

    @Transactional
    @Override
    public Currency create(Currency currency) {
        UUID id = generator.generateUUID();
        LocalDateTime now = generator.now();

        currency.setId(id);
        currency.setDtCreate(now);
        currency.setDtUpdate(now);

        CurrencyEntity entity = conversionService.convert(currency, CurrencyEntity.class);
        CurrencyEntity savedEntity = currencyRepository.save(entity);
        return conversionService.convert(savedEntity, Currency.class);
    }

    @Override
    public Page<Currency> get(Pageable pageable) {
        Page<CurrencyEntity> entities = currencyRepository.findByOrderByTitle(pageable);

        List<Currency> currencies = entities.stream()
                .map(entity -> conversionService.convert(entity, Currency.class))
                .collect(Collectors.toList());

        return new PageImpl<>(currencies, pageable, entities.getTotalElements());
    }

    @Override
    public Currency get(UUID uuid) {
        CurrencyEntity entity = currencyRepository.findById(uuid).orElse(null);

        return entity != null ? conversionService.convert(entity, Currency.class) : null;
    }

    @Override
    public Page<Currency> get(Collection<UUID> collectionId, Pageable pageable) {
        if (collectionId == null || collectionId.isEmpty()) {
            return get(pageable);
        }

        Page<CurrencyEntity> entities = currencyRepository.findByIdInOrderByTitle(collectionId, pageable);

        List<Currency> currencies = entities.stream()
                .map(entity -> conversionService.convert(entity, Currency.class))
                .collect(Collectors.toList());

        return new PageImpl<>(currencies, pageable, entities.getTotalElements());
    }
}
