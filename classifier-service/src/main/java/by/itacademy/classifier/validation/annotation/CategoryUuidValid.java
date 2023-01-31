package by.itacademy.classifier.validation.annotation;

import by.itacademy.classifier.validation.validator.CategoryUuidValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CategoryUuidValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CategoryUuidValid {
    String message() default "Categories' is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
