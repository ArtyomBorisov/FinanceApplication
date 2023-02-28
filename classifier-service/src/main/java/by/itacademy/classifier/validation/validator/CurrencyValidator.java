package by.itacademy.classifier.validation.validator;

import by.itacademy.classifier.constant.MessageError;
import by.itacademy.classifier.dto.Currency;
import by.itacademy.classifier.service.UserHolder;
import by.itacademy.classifier.validation.annotation.CustomValid;
import by.itacademy.classifier.validation.helper.CurrencyValidatorHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CurrencyValidator implements ConstraintValidator<CustomValid, Currency> {

    private final CurrencyValidatorHelper helper;
    private final UserHolder userHolder;

    public CurrencyValidator(CurrencyValidatorHelper helper, UserHolder userHolder) {
        this.helper = helper;
        this.userHolder = userHolder;
    }

    @Override
    public boolean isValid(Currency currency, ConstraintValidatorContext context) {
        if (!userHolder.isAdmin()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MessageError.FORBIDDEN)
                    .addConstraintViolation();
            return false;
        }

        if (currency == null) {
            return false;
        }

        boolean titleValid = helper.isTitleValid(currency.getTitle(), context);
        boolean descriptionValid = helper.idDescriptionValid(currency.getDescription(), context);

        return titleValid && descriptionValid;
    }
}
