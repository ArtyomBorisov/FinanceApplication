package by.itacademy.report.validation.annotation;

import by.itacademy.report.validation.validator.ParamsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ParamsValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomValid {
    String message() default "Object is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
