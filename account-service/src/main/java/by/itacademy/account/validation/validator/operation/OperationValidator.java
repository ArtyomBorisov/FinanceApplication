package by.itacademy.account.validation.validator.operation;

import by.itacademy.account.dto.Operation;
import by.itacademy.account.validation.annotation.CustomValid;
import by.itacademy.account.validation.validator.helper.ClassifierValidatorHelper;
import by.itacademy.account.validation.validator.helper.OperationValidatorHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OperationValidator implements ConstraintValidator<CustomValid, Operation> {

    private final OperationValidatorHelper operationHelper;
    private final ClassifierValidatorHelper classifierHelper;

    public OperationValidator(OperationValidatorHelper operationHelper,
                              ClassifierValidatorHelper classifierHelper) {
        this.operationHelper = operationHelper;
        this.classifierHelper = classifierHelper;
    }

    @Override
    public boolean isValid(Operation operation, ConstraintValidatorContext context) {
        if (operation == null) {
            return false;
        }

        boolean currencyIdValid = classifierHelper.isCurrencyIdExist(operation.getCurrency(), context);
        boolean categoryIdValid = classifierHelper.isCategoryIdExist(operation.getCategory(), context);
        boolean valueValid = operationHelper.isValueValid(operation.getValue(), context);

        return currencyIdValid && categoryIdValid && valueValid;
    }
}
