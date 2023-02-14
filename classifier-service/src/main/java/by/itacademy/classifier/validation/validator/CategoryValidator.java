package by.itacademy.classifier.validation.validator;

import by.itacademy.classifier.constant.MessageError;
import by.itacademy.classifier.dto.Category;
import by.itacademy.classifier.service.UserHolder;
import by.itacademy.classifier.validation.annotation.CustomValid;
import by.itacademy.classifier.validation.helper.CategoryValidatorHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CategoryValidator implements ConstraintValidator<CustomValid, Category> {

    private final CategoryValidatorHelper helper;
    private final UserHolder userHolder;

    public CategoryValidator(CategoryValidatorHelper helper, UserHolder userHolder) {
        this.helper = helper;
        this.userHolder = userHolder;
    }

    @Override
    public boolean isValid(Category category, ConstraintValidatorContext context) {
        if (category == null) {
            return false;
        }

        if (!userHolder.isAdmin()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MessageError.FORBIDDEN)
                    .addConstraintViolation();
            return false;
        }

        return helper.isTitleValid(category.getTitle(), context);
    }
}
