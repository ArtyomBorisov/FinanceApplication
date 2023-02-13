package by.itacademy.classifier.validation.helper.impl;

import by.itacademy.classifier.dao.CategoryRepository;
import by.itacademy.classifier.validation.helper.CategoryValidatorHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

import static by.itacademy.classifier.constant.FieldName.TITLE;
import static by.itacademy.classifier.constant.MessageError.*;

@Component
@Transactional(readOnly = true)
public class CategoryValidatorHelperImpl implements CategoryValidatorHelper {

    private final CategoryRepository repository;

    public CategoryValidatorHelperImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isTitleValid(String title, ConstraintValidatorContext context) {
        if (title == null || title.isEmpty()) {
            addConstraintViolation(MISSING_FIELD, TITLE, context);
            return false;
        }

        if (repository.findByTitle(title).isPresent()) {
            addConstraintViolation(NO_UNIQUE_FIELD, TITLE, context);
            return false;
        }

        return true;
    }

    @Override
    public boolean isCategoryIdExist(UUID id, ConstraintValidatorContext context) {
        if (!repository.existsCategoryEntityById(id)) {
            addConstraintViolation(ID_NOT_EXIST, id.toString(), context);
            return false;
        }

        return true;
    }

    private void addConstraintViolation(String message, String name, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(name)
                .addConstraintViolation();
    }
}
