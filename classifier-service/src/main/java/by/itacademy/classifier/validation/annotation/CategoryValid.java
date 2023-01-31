package by.itacademy.classifier.validation.annotation;

import by.itacademy.classifier.validation.validator.CategoryValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CategoryValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CategoryValid {
    String message() default "Category is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
