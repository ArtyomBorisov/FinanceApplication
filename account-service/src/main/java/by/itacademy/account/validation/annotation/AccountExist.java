package by.itacademy.account.validation.annotation;

import by.itacademy.account.validation.validator.account.AccountIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AccountIdValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountExist {
    String message() default "Object is not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
