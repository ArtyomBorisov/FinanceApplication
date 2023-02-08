package by.itacademy.user.controller.advice;

import by.itacademy.user.constant.MessageError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionAdvice {

    private final String ERROR = "error";
    private final String STRUCTURED_ERROR = "structured_error";
    private final String LOGGER_MESSAGE = "{}: {}";
    private final Logger logger = LogManager.getLogger();

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> invalidRequestParamsHandler(ConstraintViolationException e) {
        logger.error(LOGGER_MESSAGE, e.getClass().getSimpleName(), e.getMessage());

        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        int size = violations.size();

        if (size == 0) {
            return new ResponseEntity<>(new SingleResponseError(ERROR,
                    MessageError.INCORRECT_PARAMS),
                    HttpStatus.BAD_REQUEST);

        } else {
            Function<ConstraintViolation<?>, ValidationError> function =
                    violation -> new ValidationError(
                            violation.getPropertyPath().toString(),
                            violation.getMessage());

            List<ValidationError> errors = violations.stream()
                    .map(function)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(new MultipleResponseError(STRUCTURED_ERROR, errors),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exceptionHandler(Exception e) {
        logger.error(LOGGER_MESSAGE, e.getClass().getSimpleName(), e.getMessage());

        return new ResponseEntity<>(new SingleResponseError(ERROR, MessageError.SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
