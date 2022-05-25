package by.itacademy.classifier.controller.web.controllers.advice;

import by.itacademy.classifier.service.api.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> validationHandler(ValidationException e) {
        if (e.getErrors().isEmpty()) {
            return new ResponseEntity<>(new SingleResponseError("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(new MultipleResponseError("structured_error", e.getErrors()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> argumentHandler(IllegalArgumentException e) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
