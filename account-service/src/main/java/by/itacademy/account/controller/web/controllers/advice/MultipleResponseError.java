package by.itacademy.account.controller.web.controllers.advice;

import by.itacademy.account.service.api.ValidationError;

import java.util.List;

public class MultipleResponseError {
    private String logref;
    private List<ValidationError> errors;

    public MultipleResponseError(String logref, List<ValidationError> errors) {
        this.logref = logref;
        this.errors = errors;
    }

    public String getLogref() {
        return logref;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
