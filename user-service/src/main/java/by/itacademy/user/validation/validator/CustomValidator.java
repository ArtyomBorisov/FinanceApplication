package by.itacademy.user.validation.validator;

import by.itacademy.user.dto.LoginDto;
import by.itacademy.user.validation.annotation.CustomValid;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static by.itacademy.user.constant.FieldName.LOGIN;
import static by.itacademy.user.constant.FieldName.PASSWORD;
import static by.itacademy.user.constant.MessageError.LOGIN_BUSY;
import static by.itacademy.user.constant.MessageError.MISSING_FIELD;

public class CustomValidator implements ConstraintValidator<CustomValid, LoginDto> {

    private final UserDetailsManager manager;

    public CustomValidator(UserDetailsManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean isValid(LoginDto loginDto, ConstraintValidatorContext context) {
        if (loginDto == null) {
            return false;
        }

        boolean valid = true;
        String login = loginDto.getLogin();
        String password = loginDto.getPassword();

        if (isEmptyOrNull(login)) {
            addConstraintViolation(MISSING_FIELD, LOGIN, context);
            valid = false;
        } else if (manager.userExists(login)) {
            addConstraintViolation(LOGIN_BUSY, LOGIN, context);
            valid = false;
        }

        if (isEmptyOrNull(password)) {
            addConstraintViolation(MISSING_FIELD, PASSWORD, context);
            valid = false;
        }

        return valid;
    }

    private boolean isEmptyOrNull(String str) {
        return str == null || str.isEmpty();
    }

    private void addConstraintViolation(String message, String name, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(name)
                .addConstraintViolation();
    }
}
