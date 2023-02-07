package by.itacademy.classifier.validation.validator;

import by.itacademy.classifier.dto.Category;
import by.itacademy.classifier.validation.annotation.CustomValid;
import by.itacademy.classifier.validation.helper.CategoryValidatorHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CategoryValidator implements ConstraintValidator<CustomValid, Category> {

    private final CategoryValidatorHelper helper;

    public CategoryValidator(CategoryValidatorHelper helper) {
        this.helper = helper;
    }

    @Override
    public boolean isValid(Category category, ConstraintValidatorContext context) {
        if (category == null) {
            return false;
        }

        return helper.isTitleValid(category.getTitle(), context);
    }
}
