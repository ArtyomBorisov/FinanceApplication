package by.itacademy.account.controller.advice;

import java.util.List;

public class MultipleResponseError {
    private final String logref;
    private final List<ValidationError> errors;

    public MultipleResponseError(String logref, List<ValidationError> errors) {
        this.logref = logref;
        this.errors = errors;
    }

    public MultipleResponseError(List<ValidationError> errors) {
        this.logref = "structured_error";
        this.errors = errors;
    }

    public String getLogref() {
        return logref;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
