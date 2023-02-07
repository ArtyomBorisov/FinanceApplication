package by.itacademy.classifier.validation.validator;

import by.itacademy.classifier.validation.annotation.CurrencyExist;
import by.itacademy.classifier.validation.helper.CurrencyValidatorHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.UUID;

public class CurrencyIdCollectionValidator implements ConstraintValidator<CurrencyExist, Collection<UUID>> {

    private final CurrencyValidatorHelper helper;

    public CurrencyIdCollectionValidator(CurrencyValidatorHelper helper) {
        this.helper = helper;
    }

    @Override
    public boolean isValid(Collection<UUID> uuids, ConstraintValidatorContext context) {
        if (uuids == null) {
            return true;
        }

        boolean valid = true;

        for (UUID uuid : uuids) {
            if (!helper.isCurrencyIdExist(uuid, context)) {
                valid = false;
            }
        }

        return valid;
    }
}
