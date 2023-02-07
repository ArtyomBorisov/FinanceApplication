package by.itacademy.account.validation.validator.helper;

import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public interface OperationValidatorHelper {
    boolean isValueValid(double value, ConstraintValidatorContext context);

    boolean isOperationIdExist(UUID idOperation,
                               UUID idAccount,
                               String userLogin,
                               ConstraintValidatorContext context);
}
