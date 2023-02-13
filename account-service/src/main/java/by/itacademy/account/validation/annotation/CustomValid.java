package by.itacademy.account.validation.annotation;

import by.itacademy.account.validation.validator.account.AccountValidator;
import by.itacademy.account.validation.validator.operation.OperationValidator;
import by.itacademy.account.validation.validator.operation.ParamsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {AccountValidator.class, OperationValidator.class, ParamsValidator.class})
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomValid {
    String message() default "Object is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
