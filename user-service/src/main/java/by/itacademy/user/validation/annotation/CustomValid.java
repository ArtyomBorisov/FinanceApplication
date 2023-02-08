package by.itacademy.user.validation.annotation;

import by.itacademy.user.validation.validator.CustomValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {CustomValidator.class})
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomValid {
    String message() default "Object is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
