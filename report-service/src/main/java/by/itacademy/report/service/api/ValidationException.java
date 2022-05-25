package by.itacademy.report.service.api;

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

    public int getCountErrors() {
        return errors.size();
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
