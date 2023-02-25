package by.itacademy.mail.scheduler.controller.advice;

import by.itacademy.mail.scheduler.exception.MonthlyReportExistException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static by.itacademy.mail.scheduler.constant.MessageError.*;

@ControllerAdvice
public class ExceptionAdvice {

    private final Logger logger = LogManager.getLogger();

    @ExceptionHandler({MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SingleResponseError invalidRequestParamHandler(Exception e) {
        logException(e);
        return new SingleResponseError(INCORRECT_PARAMS);
    }

    @ExceptionHandler(MonthlyReportExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SingleResponseError monthlyReportExistHandler(MonthlyReportExistException e) {
        logException(e);
        return new SingleResponseError(MONTHLY_REPORT_EXIST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SingleResponseError exceptionHandler(Exception e) {
        logException(e);
        return new SingleResponseError(SERVER_ERROR);
    }

    private void logException(Exception e) {
        logger.error("{}: {}", e.getClass().getSimpleName(), e.getMessage());
    }
}
