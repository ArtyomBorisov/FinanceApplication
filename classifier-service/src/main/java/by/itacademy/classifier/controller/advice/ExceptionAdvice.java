package by.itacademy.classifier.controller.advice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static by.itacademy.classifier.constant.MessageError.*;

@ControllerAdvice
public class ExceptionAdvice {

    private static final String LOGGER_MESSAGE = "{}: {}";
    private final Logger logger = LogManager.getLogger();

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> invalidRequestParamsHandler(ConstraintViolationException e) {
        logger.error(LOGGER_MESSAGE, e.getClass().getSimpleName(), e.getMessage());

        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();

        if (violations.stream().anyMatch(violation -> FORBIDDEN.equals(violation.getMessage()))) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return violations.isEmpty() ?
                new ResponseEntity<>(
                        new SingleResponseError(INCORRECT_PARAMS), HttpStatus.BAD_REQUEST) :

                new ResponseEntity<>(
                        new MultipleResponseError(getErrors(violations)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exceptionHandler(Exception e) {
        logger.error(LOGGER_MESSAGE, e.getClass().getSimpleName(), e.getMessage());

        return new ResponseEntity<>(
                new SingleResponseError(SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private List<ValidationError> getErrors(Set<ConstraintViolation<?>> violations) {
        Function<ConstraintViolation<?>, ValidationError> function =
                violation -> new ValidationError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                );

        return violations.stream()
                .map(function)
                .collect(Collectors.toList());
    }
}
