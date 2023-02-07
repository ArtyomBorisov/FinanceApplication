package by.itacademy.classifier.validation.helper;

import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public interface CategoryValidatorHelper {
    boolean isTitleValid(String title, ConstraintValidatorContext context);

    boolean isCategoryIdExist(UUID id, ConstraintValidatorContext context);
}
