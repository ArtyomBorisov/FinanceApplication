package by.itacademy.mail.scheduler.exception;

public class MonthlyReportExistException extends RuntimeException {
    public MonthlyReportExistException() {
    }

    public MonthlyReportExistException(String message) {
        super(message);
    }

    public MonthlyReportExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public MonthlyReportExistException(Throwable cause) {
        super(cause);
    }

    public MonthlyReportExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
