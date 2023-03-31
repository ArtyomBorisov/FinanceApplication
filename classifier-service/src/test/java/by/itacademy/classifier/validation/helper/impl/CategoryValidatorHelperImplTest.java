package by.itacademy.classifier.validation.helper.impl;

import by.itacademy.classifier.dao.CategoryRepository;
import by.itacademy.classifier.dao.entity.CategoryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryValidatorHelperImplTest {
    @Mock
    private CategoryRepository repository;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilderCustomizableContext;

    @InjectMocks
    private CategoryValidatorHelperImpl categoryValidatorHelper;

    @DisplayName("Method \"boolean isTitleValid(...)\": valid title")
    @Test
    void givenValidTitle_whenCheckTitle_thenReturnTrue() {
        String title = "okay";
        given(repository.findByTitle(title))
                .willReturn(Optional.empty());

        boolean answer = categoryValidatorHelper.isTitleValid(title, context);

        assertTrue(answer);
    }

    @DisplayName("Method \"boolean isTitleValid(...)\": invalid title")
    @Test
    void givenInvalidTitle_whenCheckTitle_thenReturnFalse() {
        givenContext();

        String str = null;
        boolean answer1 = categoryValidatorHelper.isTitleValid(str, context);
        boolean answer2 = categoryValidatorHelper.isTitleValid("", context);

        assertFalse(answer1);
        assertFalse(answer2);
    }

    @DisplayName("Method \"boolean isTitleValid(...)\": existing title")
    @Test
    void givenExistingTitle_whenCheckTitle_thenReturnFalse() {
        String title = "exist";
        given(repository.findByTitle(title))
                .willReturn(Optional.of(new CategoryEntity()));
        givenContext();

        boolean answer = categoryValidatorHelper.isTitleValid(title, context);

        assertFalse(answer);
    }

    @DisplayName("Method \"boolean isCategoryIdExist(...)\": existing id\"")
    @Test
    void givenExistingId_whenCheckId_thenReturnTrue() {
        UUID id = UUID.randomUUID();
        given(repository.existsCategoryEntityById(id))
                .willReturn(true);

        boolean answer = categoryValidatorHelper.isCategoryIdExist(id, context);

        assertTrue(answer);
    }

    @DisplayName("Method \"boolean isCategoryIdExist(...)\": nonexistent id\"")
    @Test
    void givenNonexistentId_whenCheckId_thenReturnTrue() {
        UUID id = UUID.randomUUID();
        given(repository.existsCategoryEntityById(id))
                .willReturn(false);
        givenContext();

        boolean answer = categoryValidatorHelper.isCategoryIdExist(id, context);

        assertFalse(answer);
    }

    private void givenContext() {
        given(context.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .willReturn(builder);
        given(builder.addPropertyNode(Mockito.anyString()))
                .willReturn(nodeBuilderCustomizableContext);
        given(nodeBuilderCustomizableContext.addConstraintViolation())
                .willReturn(context);
    }
}
