package by.itacademy.account.scheduler.validation.annotation;

import by.itacademy.account.scheduler.validation.validator.OperationIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OperationIdValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Exist {
    String message() default "Object is not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
