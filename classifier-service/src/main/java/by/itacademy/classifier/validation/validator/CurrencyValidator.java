package by.itacademy.classifier.validation.validator;

import by.itacademy.classifier.constant.FieldName;
import by.itacademy.classifier.constant.MessageError;
import by.itacademy.classifier.dto.Currency;
import by.itacademy.classifier.repository.CurrencyRepository;
import by.itacademy.classifier.validation.annotation.CurrencyValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CurrencyValidator implements ConstraintValidator<CurrencyValid, Currency> {

    private final CurrencyRepository repository;

    public CurrencyValidator(CurrencyRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isValid(Currency currency, ConstraintValidatorContext context) {
        if (currency == null) {
            return false;
        }

        boolean valid = true;

        String title = currency.getTitle();
        if (nullOrEmpty(title)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MessageError.MISSING_FIELD)
                    .addPropertyNode(FieldName.TITLE)
                    .addConstraintViolation();
            valid = false;

        } else if (repository.findByTitle(title).isPresent()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MessageError.NO_UNIQUE_FIELD)
                    .addPropertyNode(FieldName.TITLE)
                    .addConstraintViolation();
            valid = false;
        }

        if (nullOrEmpty(currency.getDescription())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MessageError.MISSING_FIELD)
                    .addPropertyNode(FieldName.DESCRIPTION)
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }

    private boolean nullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
