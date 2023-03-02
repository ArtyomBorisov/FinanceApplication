package by.itacademy.classifier.service.impl;

import by.itacademy.classifier.dto.Currency;
import by.itacademy.classifier.dao.CurrencyRepository;
import by.itacademy.classifier.dao.entity.CurrencyEntity;
import by.itacademy.classifier.service.ClassifierService;
import by.itacademy.classifier.utils.Generator;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

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
        generateIdAndTimeAndAddToCurrency(currency);
        CurrencyEntity entityForSaving = conversionService.convert(currency, CurrencyEntity.class);
        CurrencyEntity savedEntity = currencyRepository.save(entityForSaving);
        return conversionService.convert(savedEntity, Currency.class);
    }

    @Override
    public Page<Currency> get(Pageable pageable) {
        Page<CurrencyEntity> entities = currencyRepository.findByOrderByTitle(pageable);
        return getCurrencyPage(entities);
    }

    @Override
    public Currency get(UUID uuid) {
        return currencyRepository.findById(uuid)
                .map(entity -> conversionService.convert(entity, Currency.class))
                .orElse(null);
    }

    @Override
    public Page<Currency> get(Collection<UUID> collectionId, Pageable pageable) {
        if (CollectionUtils.isEmpty(collectionId)) {
            return get(pageable);
        }

        Page<CurrencyEntity> entities = currencyRepository.findByIdInOrderByTitle(collectionId, pageable);
        return getCurrencyPage(entities);
    }

    private void generateIdAndTimeAndAddToCurrency(Currency currency) {
        UUID id = generator.generateUUID();
        LocalDateTime now = generator.now();
        currency.setId(id);
        currency.setDtCreate(now);
        currency.setDtUpdate(now);
    }

    private Page<Currency> getCurrencyPage(Page<CurrencyEntity> entities) {
        return entities.map(entity -> conversionService.convert(entity, Currency.class));
    }
}
