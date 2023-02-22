package by.itacademy.mail.controller.advice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static by.itacademy.mail.constant.MessageError.INCORRECT_PARAMS;
import static by.itacademy.mail.constant.MessageError.SERVER_ERROR;

@ControllerAdvice
public class ExceptionAdvice {

    private final Logger logger = LogManager.getLogger();

    @ExceptionHandler({MethodArgumentNotValidException.class, MailSendException.class,
            MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<?> invalidRequestParamsHandler(Exception e) {
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
}
