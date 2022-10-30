package by.itacademy.mail.controller.web.controllers.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {

    private final String error = "error";

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> validationHandler(IllegalArgumentException e) {
        return new ResponseEntity<>(new SingleResponseError(error, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> exceptionHandler(RuntimeException e) {
        return new ResponseEntity<>(new SingleResponseError(error,
                "Сервер не смог корректно обработать запрос. Пожалуйста обратитесь к администратору"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
