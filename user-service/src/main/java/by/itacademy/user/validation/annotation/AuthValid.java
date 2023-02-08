package by.itacademy.user.validation.annotation;

import by.itacademy.user.validation.validator.AuthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {AuthValidator.class})
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthValid {
    String message() default "Authentication is unsuccessful";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
