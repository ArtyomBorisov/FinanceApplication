package by.itacademy.classifier.validation.validator;

import by.itacademy.classifier.dto.Currency;
import by.itacademy.classifier.validation.annotation.CustomValid;
import by.itacademy.classifier.validation.helper.CurrencyValidatorHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CurrencyValidator implements ConstraintValidator<CustomValid, Currency> {

    private final CurrencyValidatorHelper helper;

    public CurrencyValidator(CurrencyValidatorHelper helper) {
        this.helper = helper;
    }

    @Override
    public boolean isValid(Currency currency, ConstraintValidatorContext context) {
        if (currency == null) {
            return false;
        }

        boolean titleValid = helper.isTitleValid(currency.getTitle(), context);
        boolean descriptionValid = helper.idDescriptionValid(currency.getDescription(), context);

        return titleValid && descriptionValid;
    }
}
