package by.itacademy.account.validation.annotation;

import by.itacademy.account.validation.validator.account.AccountUpdatingValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AccountUpdatingValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountUpdatingValid {
    String message() default "Account for updating is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
