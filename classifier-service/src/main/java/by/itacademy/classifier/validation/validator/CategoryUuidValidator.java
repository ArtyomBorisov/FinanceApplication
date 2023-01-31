package by.itacademy.classifier.validation.validator;

import by.itacademy.classifier.constant.MessageError;
import by.itacademy.classifier.repository.CategoryRepository;
import by.itacademy.classifier.validation.annotation.CategoryUuidValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.UUID;

public class CategoryUuidValidator implements ConstraintValidator<CategoryUuidValid, Collection<UUID>> {

    private final CategoryRepository repository;

    public CategoryUuidValidator(CategoryRepository repository) {
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
                        .addPropertyNode(uuids.toString())
                        .addConstraintViolation();
                valid = false;
            }
        }

        return valid;
    }
}
