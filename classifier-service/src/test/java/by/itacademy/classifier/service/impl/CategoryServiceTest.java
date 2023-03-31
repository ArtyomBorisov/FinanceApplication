package by.itacademy.classifier.service.impl;

import by.itacademy.classifier.dao.CategoryRepository;
import by.itacademy.classifier.dao.entity.CategoryEntity;
import by.itacademy.classifier.dto.Category;
import by.itacademy.classifier.utils.Generator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository repository;
    @Mock
    private ConversionService conversionService;
    @Mock
    private Generator generator;
    @InjectMocks
    private CategoryService categoryService;

    private Category categoryForSaving;
    private Category categoryFromDB;
    private CategoryEntity categoryEntity;

    private final LocalDateTime dateTime = LocalDateTime.now();
    private final UUID existingId = UUID.randomUUID();
    private final UUID nonexistentId = UUID.randomUUID();
    private final Pageable pageable = Pageable.ofSize(20).withPage(0);

    @BeforeEach
    void createObjects() {
        final String title = "Книги";
        categoryForSaving = Category.Builder.createBuilder()
                .setTitle(title)
                .build();

        categoryFromDB = Category.Builder.createBuilder()
                .setId(existingId)
                .setDtCreate(dateTime)
                .setDtUpdate(dateTime)
                .setTitle(title)
                .build();

        categoryEntity = CategoryEntity.Builder.createBuilder()
                .setId(existingId)
                .setDtCreate(dateTime)
                .setDtUpdate(dateTime)
                .setTitle(title)
                .build();
    }

    @DisplayName("Method \"Category create(Category category)\"")
    @Test
    void givenCategory_whenCreateCategory_thenReturnCategory() {
        given(generator.generateUUID())
                .willReturn(existingId);
        given(generator.now())
                .willReturn(dateTime);
        given(conversionService.convert(categoryForSaving, CategoryEntity.class))
                .willReturn(categoryEntity);
        given(conversionService.convert(categoryEntity, Category.class))
                .willReturn(categoryFromDB);
        given(repository.save(categoryEntity))
                .willReturn(categoryEntity);

        Category createdCategory = categoryService.create(categoryForSaving);

        assertNotNull(createdCategory);
        assertEquals(categoryFromDB, createdCategory);
    }

    @DisplayName("Method \"Page<Category> get(Pageable pageable)\": NOT EMPTY page")
    @Test
    void givenPageable_whenGet_thenReturnPage() {
        Page<CategoryEntity> entities = getPageWithEntity(categoryEntity);
        given(repository.findByOrderByTitle(pageable))
                .willReturn(entities);
        given(conversionService.convert(categoryEntity, Category.class))
                .willReturn(categoryFromDB);

        Page<Category> actual = categoryService.get(pageable);

        Page<Category> expected = getPageWithCategory(categoryFromDB);
        assertEquals(expected, actual);
    }

    @DisplayName("Method \"Page<Category> get(Pageable pageable)\": EMPTY page")
    @Test
    void givenPageable_whenGet_thenReturnEmptyPage() {
        Page<CategoryEntity> entities = getEmptyPageWithEntity();
        given(repository.findByOrderByTitle(pageable))
                .willReturn(entities);

        Page<Category> actual = categoryService.get(pageable);

        Page<Category> expected = getEmptyPageWithCategory();
        assertEquals(expected, actual);
    }

    @DisplayName("Method \"Category get(UUID uuid)\": EXISTENT uuid")
    @Test
    void givenExistentCategoryId_whenGetById_thenReturnCategory() {
        given(repository.findById(existingId))
                .willReturn(Optional.of(categoryEntity));
        given(conversionService.convert(categoryEntity, Category.class))
                .willReturn(categoryFromDB);

        Category category = categoryService.get(existingId);

        assertEquals(categoryFromDB, category);
    }

    @DisplayName("Method \"Category get(UUID uuid)\": NONEXISTENT uuid")
    @Test
    void givenNonexistentCategoryId_whenGetById_thenReturnNull() {
        given(repository.findById(nonexistentId))
                .willReturn(Optional.empty());

        Category nullCategory = categoryService.get(nonexistentId);

        assertNull(nullCategory);
    }

    private Page<Category> getPageWithCategory(Category category) {
        return new PageImpl<>(List.of(category), pageable, 1);
    }

    private Page<Category> getEmptyPageWithCategory() {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    private Page<CategoryEntity> getPageWithEntity(CategoryEntity entity) {
        return new PageImpl<>(List.of(entity), pageable, 1);
    }

    private Page<CategoryEntity> getEmptyPageWithEntity() {
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }
}
