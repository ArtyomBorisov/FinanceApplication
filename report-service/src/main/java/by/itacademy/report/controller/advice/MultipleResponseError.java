package by.itacademy.report.controller.advice;

import java.util.List;

public class MultipleResponseError {
    private String logref = "structured_error";
    private List<ValidationError> errors;

    public MultipleResponseError(String logref, List<ValidationError> errors) {
        this.logref = logref;
        this.errors = errors;
    }

    public MultipleResponseError(List<ValidationError> errors) {
        this.errors = errors;
    }

    public String getLogref() {
        return logref;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
