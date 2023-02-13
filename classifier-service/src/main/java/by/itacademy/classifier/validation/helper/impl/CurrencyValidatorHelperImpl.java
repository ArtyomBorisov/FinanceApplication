package by.itacademy.classifier.validation.helper.impl;

import by.itacademy.classifier.dao.CurrencyRepository;
import by.itacademy.classifier.validation.helper.CurrencyValidatorHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

import static by.itacademy.classifier.constant.FieldName.DESCRIPTION;
import static by.itacademy.classifier.constant.FieldName.TITLE;
import static by.itacademy.classifier.constant.MessageError.*;

@Component
@Transactional(readOnly = true)
public class CurrencyValidatorHelperImpl implements CurrencyValidatorHelper {

    private final CurrencyRepository repository;

    public CurrencyValidatorHelperImpl(CurrencyRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isTitleValid(String title, ConstraintValidatorContext context) {
        if (nullOrEmpty(title)) {
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
    public boolean idDescriptionValid(String description, ConstraintValidatorContext context) {
        if (nullOrEmpty(description)) {
            addConstraintViolation(MISSING_FIELD, DESCRIPTION, context);
            return false;
        }

        return true;
    }

    @Override
    public boolean isCurrencyIdExist(UUID id, ConstraintValidatorContext context) {
        if (!repository.existsCurrencyEntityById(id)) {
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

    private boolean nullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
