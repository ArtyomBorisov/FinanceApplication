package by.itacademy.user.validation.validator;

import by.itacademy.user.dto.LoginDto;
import by.itacademy.user.validation.annotation.AuthValid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static by.itacademy.user.constant.FieldName.*;
import static by.itacademy.user.constant.MessageError.LOGIN_OR_PASSWORD_INCORRECT;
import static by.itacademy.user.constant.MessageError.MISSING_FIELD;

public class AuthValidator implements ConstraintValidator<AuthValid, LoginDto> {

    private final UserDetailsManager manager;
    private final PasswordEncoder encoder;

    public AuthValidator(UserDetailsManager manager, PasswordEncoder encoder) {
        this.manager = manager;
        this.encoder = encoder;
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
        }

        if (isEmptyOrNull(password)) {
            addConstraintViolation(MISSING_FIELD, PASSWORD, context);
            valid = false;
        }

        if (!valid) {
            return false;
        }

        if (!manager.userExists(login)) {
            addConstraintViolation(LOGIN_OR_PASSWORD_INCORRECT, LOGIN_AND_PASSWORD, context);
            return false;
        }

        UserDetails details = manager.loadUserByUsername(loginDto.getLogin());

        if (!encoder.matches(loginDto.getPassword(), details.getPassword())
                || !details.isEnabled()) {
            addConstraintViolation(LOGIN_OR_PASSWORD_INCORRECT, LOGIN_AND_PASSWORD, context);
            return false;
        }

        return true;
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
