package by.itacademy.classifier.validation.validator;

import by.itacademy.classifier.validation.annotation.CurrencyExist;
import by.itacademy.classifier.validation.helper.CurrencyValidatorHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public class CurrencyIdValidator implements ConstraintValidator<CurrencyExist, UUID> {

    private final CurrencyValidatorHelper helper;

    public CurrencyIdValidator(CurrencyValidatorHelper helper) {
        this.helper = helper;
    }

    @Override
    public boolean isValid(UUID id, ConstraintValidatorContext context) {
        return id != null && helper.isCurrencyIdExist(id, context);
    }
}
