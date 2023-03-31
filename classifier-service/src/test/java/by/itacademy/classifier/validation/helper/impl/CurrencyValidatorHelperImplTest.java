package by.itacademy.classifier.validation.helper.impl;

import by.itacademy.classifier.dao.CurrencyRepository;
import by.itacademy.classifier.dao.entity.CurrencyEntity;
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

class CurrencyValidatorHelperImplTest {
    @Mock
    private CurrencyRepository repository;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilderCustomizableContext;

    @InjectMocks
    private CurrencyValidatorHelperImpl currencyValidatorHelper;

    @DisplayName("Method \"boolean isTitleValid(...)\": valid title")
    @Test
    void givenValidTitle_whenCheckTitle_thenReturnTrue() {
        String title = "okay";
        given(repository.findByTitle(title))
                .willReturn(Optional.empty());

        boolean answer = currencyValidatorHelper.isTitleValid("okay", context);

        assertTrue(answer);
    }

    @DisplayName("Method \"boolean isTitleValid(...)\": invalid title")
    @Test
    void givenInvalidTitle_whenCheckTitle_thenReturnFalse() {
        givenContext();

        String str = null;
        boolean answer1 = currencyValidatorHelper.isTitleValid(str, context);
        boolean answer2 = currencyValidatorHelper.isTitleValid("", context);

        assertFalse(answer1);
        assertFalse(answer2);
    }

    @DisplayName("Method \"boolean isDescriptionValid(...)\": valid description")
    @Test
    void givenValidDescription_whenCheckDescription_thenReturnTrue() {
        boolean answer = currencyValidatorHelper.isTitleValid("okay", context);

        assertTrue(answer);
    }

    @DisplayName("Method \"boolean isDescriptionValid(...)\": invalid description")
    @Test
    void givenInvalidDescription_whenCheckDescription_thenReturnFalse() {
        givenContext();

        String str = null;
        boolean answer1 = currencyValidatorHelper.isTitleValid(str, context);
        boolean answer2 = currencyValidatorHelper.isTitleValid("", context);

        assertFalse(answer1);
        assertFalse(answer2);
    }

    @DisplayName("Method \"boolean isTitleValid(...)\": existing title")
    @Test
    void givenExistingTitle_whenCheckTitle_thenReturnFalse() {
        String title = "exist";
        given(repository.findByTitle(title))
                .willReturn(Optional.of(new CurrencyEntity()));
        givenContext();

        boolean answer = currencyValidatorHelper.isTitleValid(title, context);

        assertFalse(answer);
    }

    @DisplayName("Method \"boolean isCurrencyIdExist(...)\": existing id\"")
    @Test
    void givenExistingId_whenCheckId_thenReturnTrue() {
        UUID id = UUID.randomUUID();
        given(repository.existsCurrencyEntityById(id))
                .willReturn(true);

        boolean answer = currencyValidatorHelper.isCurrencyIdExist(id, context);

        assertTrue(answer);
    }

    @DisplayName("Method \"boolean isCurrencyIdExist(...)\": nonexistent id\"")
    @Test
    void givenNonexistentId_whenCheckId_thenReturnTrue() {
        UUID id = UUID.randomUUID();
        given(repository.existsCurrencyEntityById(id))
                .willReturn(false);
        givenContext();

        boolean answer = currencyValidatorHelper.isCurrencyIdExist(id, context);

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
