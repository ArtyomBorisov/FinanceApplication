package by.itacademy.account.controller.web.controllers.advice;

import by.itacademy.account.exception.ValidationException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionAdvice {

    private final String error = "error";

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> validationHandler(ValidationException e) {
        if (e.getErrors().isEmpty()) {
            return new ResponseEntity<>(new SingleResponseError(error, e.getMessage()), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(new MultipleResponseError("structured_error", e.getErrors()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler({InvalidFormatException.class, ConstraintViolationException.class})
    public ResponseEntity<?> invalidFormatHandler(Exception e) {
        return new ResponseEntity<>(new SingleResponseError(error,
                "Запрос содержит некорретные данные. Измените запрос и отправьте его ещё раз"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> exceptionHandler(RuntimeException e) {
        return new ResponseEntity<>(new SingleResponseError(error,
                "Сервер не смог корректно обработать запрос. Пожалуйста обратитесь к администратору"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
