package by.itacademy.account.service.api;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends IllegalArgumentException {

    private List<ValidationError> errors = new ArrayList<>();

    public ValidationException(String s, List<ValidationError> errors) {
        super(s);
        this.errors = errors;
    }

    public ValidationException() {
    }

    public ValidationException(String s) {
        super(s);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(Errors errors) {
        super(errors.name());
    }

    public ValidationException(Errors incorrectParams, List<ValidationError> errors) {
        super(incorrectParams.name());
        this.errors = errors;
    }

    public int getCountErrors() {
        return errors.size();
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
