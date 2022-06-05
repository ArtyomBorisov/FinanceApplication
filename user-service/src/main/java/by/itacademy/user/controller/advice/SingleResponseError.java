package by.itacademy.user.controller.advice;

public class SingleResponseError {
    private String logref;
    private String message;

    public SingleResponseError(String logref, String message) {
        this.logref = logref;
        this.message = message;
    }

    public String getLogref() {
        return logref;
    }

    public String getMessage() {
        return message;
    }
}
