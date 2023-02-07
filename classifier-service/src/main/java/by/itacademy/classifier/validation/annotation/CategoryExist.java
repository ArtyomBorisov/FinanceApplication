package by.itacademy.classifier.validation.annotation;

import by.itacademy.classifier.validation.validator.CategoryIdCollectionValidator;
import by.itacademy.classifier.validation.validator.CategoryIdValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {CategoryIdCollectionValidator.class, CategoryIdValidator.class})
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CategoryExist {
    String message() default "Categories' is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
