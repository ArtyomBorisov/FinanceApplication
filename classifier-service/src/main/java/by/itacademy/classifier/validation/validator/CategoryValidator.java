package by.itacademy.classifier.validation.validator;

import by.itacademy.classifier.constant.FieldName;
import by.itacademy.classifier.constant.MessageError;
import by.itacademy.classifier.dto.Category;
import by.itacademy.classifier.repository.CategoryRepository;
import by.itacademy.classifier.validation.annotation.CategoryValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CategoryValidator implements ConstraintValidator<CategoryValid, Category> {

    private final CategoryRepository repository;

    public CategoryValidator(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isValid(Category category, ConstraintValidatorContext context) {
        if (category == null) {
            return false;
        }

        boolean valid = true;

        String title = category.getTitle();
        if (title == null || title.isEmpty()) {
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

        return valid;
    }
}
