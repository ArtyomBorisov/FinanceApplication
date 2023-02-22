package by.itacademy.user.controller.advice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static by.itacademy.user.constant.MessageError.INCORRECT_PARAMS;
import static by.itacademy.user.constant.MessageError.SERVER_ERROR;

@ControllerAdvice
public class ExceptionAdvice {

    private final Logger logger = LogManager.getLogger();

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> invalidRequestParamsHandler(ConstraintViolationException e) {
        logException(e);

        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        return violations.isEmpty() ?
                new ResponseEntity<>(
                        new SingleResponseError(INCORRECT_PARAMS), HttpStatus.BAD_REQUEST) :

                new ResponseEntity<>(
                        new MultipleResponseError(getErrors(violations)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> invalidRequestParamHandler(HttpMessageNotReadableException e) {
        logException(e);

        return new ResponseEntity<>(
                new SingleResponseError(INCORRECT_PARAMS), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exceptionHandler(Exception e) {
        logException(e);

        return new ResponseEntity<>(
                new SingleResponseError(SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logException(Exception e) {
        logger.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
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
