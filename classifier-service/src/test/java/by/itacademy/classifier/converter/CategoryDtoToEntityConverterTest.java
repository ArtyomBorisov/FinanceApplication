package by.itacademy.classifier.converter;

import by.itacademy.classifier.dao.entity.CategoryEntity;
import by.itacademy.classifier.dto.Category;
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

class CategoryDtoToEntityConverterTest {
    private final CategoryDtoToEntityConverter converter = new CategoryDtoToEntityConverter();

    @DisplayName("category dto convert to category entity")
    @ParameterizedTest
    @MethodSource("params")
    void givenCategoryDto_whenConvertCategoryDto_thenReturnCategoryEntity(Category dto) {
        CategoryEntity entity = converter.convert(dto);

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getDtCreate(), entity.getDtCreate());
        assertEquals(dto.getDtUpdate(), entity.getDtUpdate());
        assertEquals(dto.getTitle(), entity.getTitle());
    }

    static Stream<Arguments> params() {
        return Stream.of(
                arguments(Category.Builder.createBuilder()
                        .setId(UUID.randomUUID())
                        .setDtCreate(LocalDateTime.now())
                        .setDtUpdate(LocalDateTime.now())
                        .setTitle("random title")
                        .build()
                ),
                arguments(new Category())
        );
    }
}
