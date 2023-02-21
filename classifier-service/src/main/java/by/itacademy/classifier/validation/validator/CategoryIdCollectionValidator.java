package by.itacademy.classifier.validation.validator;

import by.itacademy.classifier.validation.annotation.CategoryExist;
import by.itacademy.classifier.validation.helper.CategoryValidatorHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.UUID;

public class CategoryIdCollectionValidator implements ConstraintValidator<CategoryExist, Collection<UUID>> {

    private final CategoryValidatorHelper helper;

    public CategoryIdCollectionValidator(CategoryValidatorHelper helper) {
        this.helper = helper;
    }

    @Override
    public boolean isValid(Collection<UUID> uuids, ConstraintValidatorContext context) {
        if (uuids == null) {
            return true;
        }

        boolean valid = true;

        for (UUID uuid : uuids) {
            if (!helper.isCategoryIdExist(uuid, context)) valid = false;
        }

        return valid;
    }
}
