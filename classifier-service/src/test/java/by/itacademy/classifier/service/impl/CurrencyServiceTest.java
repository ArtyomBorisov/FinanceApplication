package by.itacademy.classifier.service.impl;

import by.itacademy.classifier.dao.CurrencyRepository;
import by.itacademy.classifier.dao.entity.CurrencyEntity;
import by.itacademy.classifier.dto.Currency;
import by.itacademy.classifier.utils.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {
    @Mock
    private CurrencyRepository repository;
    @Mock
    private ConversionService conversionService;
    @Mock
    private Generator generator;
    @InjectMocks
    private CurrencyService currencyService;

    private Currency currencyForSaving;
    private Currency currencyFromDB;
    private CurrencyEntity currencyEntity;

    private final LocalDateTime dateTime = LocalDateTime.now();
    private final UUID existingId = UUID.randomUUID();
    private final UUID nonexistentId = UUID.randomUUID();
    private final Pageable pageable = Pageable.ofSize(20).withPage(0);

    @BeforeEach
    void createObject() {
        final String title = "BYN";
        final String description = "Белорусский рубль";

        currencyForSaving = Currency.Builder.createBuilder()
                .setTitle(title)
                .setDescription(description)
                .build();

        currencyFromDB = Currency.Builder.createBuilder()
                .setId(existingId)
                .setDtCreate(dateTime)
                .setDtUpdate(dateTime)
                .setTitle(title)
                .setDescription(description)
                .build();

        currencyEntity = CurrencyEntity.Builder.createBuilder()
                .setId(existingId)
                .setDtCreate(dateTime)
                .setDtUpdate(dateTime)
                .setTitle(title)
                .setDescription(description)
                .build();
    }

    @DisplayName("Method \"Currency create(Currency currency)\"")
    @Test
    void givenCurrency_whenCreateCurrency_thenReturnCurrency() {
        given(generator.generateUUID())
                .willReturn(existingId);
        given(generator.now())
                .willReturn(dateTime);
        given(conversionService.convert(currencyForSaving, CurrencyEntity.class))
                .willReturn(currencyEntity);
        given(conversionService.convert(currencyEntity, Currency.class))
                .willReturn(currencyFromDB);
        given(repository.save(currencyEntity))
                .willReturn(currencyEntity);

        Currency createdCurrency = currencyService.create(currencyForSaving);

        assertNotNull(createdCurrency);
        assertEquals(currencyFromDB, createdCurrency);
    }

    @DisplayName("Method \"Page<Currency> get(Pageable pageable)\": NOT EMPTY page")
    @Test
    void givenPageable_whenGet_thenReturnPage() {
        Page<CurrencyEntity> entities = getPageWithEntity(currencyEntity);
        given(repository.findByOrderByTitle(pageable))
                .willReturn(entities);
        given(conversionService.convert(currencyEntity, Currency.class))
                .willReturn(currencyFromDB);

        Page<Currency> actual = currencyService.get(pageable);

        Page<Currency> expected = getPageWithCurrency(currencyFromDB);
        assertEquals(expected, actual);
    }

    @DisplayName("Method \"Page<Currency> get(Pageable pageable)\": EMPTY page")
    @Test
    void givenPageable_whenGet_thenReturnEmptyPage() {
        Page<CurrencyEntity> entities = getEmptyPageWithEntity();
        given(repository.findByOrderByTitle(pageable))
                .willReturn(entities);

        Page<Currency> actual = currencyService.get(pageable);

        Page<Currency> expected = getEmptyPageWithCurrency();
        assertEquals(expected, actual);
    }

    @DisplayName("Method \"Currency get(UUID uuid)\": EXISTENT uuid")
    @Test
    void givenExistentCurrencyId_whenGetById_thenReturnCurrency() {
        given(repository.findById(existingId))
                .willReturn(Optional.of(currencyEntity));
        given(conversionService.convert(currencyEntity, Currency.class))
                .willReturn(currencyFromDB);

        Currency currency = currencyService.get(existingId);

        assertEquals(currencyFromDB, currency);
    }

    @DisplayName("Method \"Currency get(UUID uuid)\": NONEXISTENT uuid")
    @Test
    void givenNonexistentCurrencyId_whenGetById_thenReturnNull() {
        given(repository.findById(nonexistentId))
                .willReturn(Optional.empty());

        Currency nullCurrency = currencyService.get(nonexistentId);

        assertNull(nullCurrency);
    }

    private Page<Currency> getPageWithCurrency(Currency currency) {
        return new PageImpl<>(List.of(currency), pageable, 1);
    }

    private Page<Currency> getEmptyPageWithCurrency() {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    private Page<CurrencyEntity> getPageWithEntity(CurrencyEntity entity) {
        return new PageImpl<>(List.of(entity), pageable, 1);
    }

    private Page<CurrencyEntity> getEmptyPageWithEntity() {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }
}























