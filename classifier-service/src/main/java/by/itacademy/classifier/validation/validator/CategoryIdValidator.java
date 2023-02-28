package by.itacademy.classifier.validation.validator;

import by.itacademy.classifier.validation.annotation.CategoryExist;
import by.itacademy.classifier.validation.helper.CategoryValidatorHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public class CategoryIdValidator implements ConstraintValidator<CategoryExist, UUID> {

    private final CategoryValidatorHelper helper;

    public CategoryIdValidator(CategoryValidatorHelper helper) {
        this.helper = helper;
    }

    @Override
    public boolean isValid(UUID id, ConstraintValidatorContext context) {
        return id != null && helper.isCategoryIdExist(id, context);
    }
}
