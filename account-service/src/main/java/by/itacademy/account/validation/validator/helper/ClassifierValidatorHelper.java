package by.itacademy.account.validation.validator.helper;

import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public interface ClassifierValidatorHelper {
    boolean isCurrencyIdExist(UUID idCurrency, ConstraintValidatorContext context);

    boolean isCategoryIdExist(UUID idCategory, ConstraintValidatorContext context);
}
