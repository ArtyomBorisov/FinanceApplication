package by.itacademy.user.service.api;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends IllegalArgumentException {

    private List<ValidationError> errors = new ArrayList<>();

    public ValidationException(String s, List<ValidationError> errors) {
        super(s);
        this.errors = errors;
    }

    public ValidationException(String s, ValidationError error) {
        super(s);
        this.errors.add(error);
    }

    public ValidationException(List<ValidationError> errors) {
        super("Переданы некорректные параметры");
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

    public int getCountErrors() {
        return errors.size();
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
