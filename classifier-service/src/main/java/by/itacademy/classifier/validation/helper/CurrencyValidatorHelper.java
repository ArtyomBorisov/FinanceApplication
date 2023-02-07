package by.itacademy.classifier.validation.helper;

import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public interface CurrencyValidatorHelper {
    boolean isTitleValid(String title, ConstraintValidatorContext context);

    boolean idDescriptionValid(String description, ConstraintValidatorContext context);

    boolean isCurrencyIdExist(UUID id, ConstraintValidatorContext context);
}
