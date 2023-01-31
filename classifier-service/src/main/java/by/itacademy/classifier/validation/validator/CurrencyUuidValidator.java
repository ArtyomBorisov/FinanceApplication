package by.itacademy.classifier.validation.validator;

import by.itacademy.classifier.constant.MessageError;
import by.itacademy.classifier.repository.CurrencyRepository;
import by.itacademy.classifier.validation.annotation.CurrencyUuidValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.UUID;

public class CurrencyUuidValidator implements ConstraintValidator<CurrencyUuidValid, Collection<UUID>> {

    private final CurrencyRepository repository;

    public CurrencyUuidValidator(CurrencyRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isValid(Collection<UUID> uuids, ConstraintValidatorContext context) {
        if (uuids == null) {
            return true;
        }

        boolean valid = true;

        for (UUID uuid : uuids) {
            if (!repository.existsCategoryEntityById(uuid)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(MessageError.ID_NOT_EXIST)
                        .addPropertyNode(uuid.toString())
                        .addConstraintViolation();
                valid = false;
            }
        }

        return valid;
    }
}
