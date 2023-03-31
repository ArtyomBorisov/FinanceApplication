package by.itacademy.classifier.converter;

import by.itacademy.classifier.dao.entity.CurrencyEntity;
import by.itacademy.classifier.dto.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CurrencyEntityToDtoConverterTest {
    private final CurrencyEntityToDtoConverter converter = new CurrencyEntityToDtoConverter();

    @DisplayName("currency entity convert to currency dto")
    @ParameterizedTest
    @MethodSource("params")
    void givenCurrencyEntity_whenConvertCurrencyEntity_thenReturnCurrencyDto(CurrencyEntity entity) {
        Currency dto = converter.convert(entity);

        assertNotNull(dto);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getDtCreate(), entity.getDtCreate());
        assertEquals(dto.getDtUpdate(), entity.getDtUpdate());
        assertEquals(dto.getTitle(), entity.getTitle());
    }

    static Stream<Arguments> params() {
        return Stream.of(
                arguments(CurrencyEntity.Builder.createBuilder()
                        .setId(UUID.randomUUID())
                        .setDtCreate(LocalDateTime.now())
                        .setDtUpdate(LocalDateTime.now())
                        .setTitle("random title")
                        .setDescription("random description")
                        .build()
                ),
                arguments(new CurrencyEntity())
        );
    }
}
