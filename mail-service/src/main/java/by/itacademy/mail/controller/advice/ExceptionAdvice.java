package by.itacademy.mail.controller.advice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static by.itacademy.mail.constant.MessageError.INCORRECT_PARAMS;
import static by.itacademy.mail.constant.MessageError.SERVER_ERROR;

@ControllerAdvice
public class ExceptionAdvice {

    private static final String LOGGER_MESSAGE = "{}: {}";
    private final Logger logger = LogManager.getLogger();

    @ExceptionHandler({MethodArgumentNotValidException.class, MailSendException.class})
    public ResponseEntity<?> invalidRequestParamsHandler(RuntimeException e) {
        logger.error(LOGGER_MESSAGE, e.getClass().getSimpleName(), e.getMessage());

        return new ResponseEntity<>(
                new SingleResponseError(INCORRECT_PARAMS), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exceptionHandler(Exception e) {
        logger.error(LOGGER_MESSAGE, e.getClass().getSimpleName(), e.getMessage());

        return new ResponseEntity<>(
                new SingleResponseError(SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
