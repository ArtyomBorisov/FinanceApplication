package by.itacademy.user.service;

import by.itacademy.user.controller.dto.LoginDto;
import by.itacademy.user.controller.utils.JwtTokenUtil;
import by.itacademy.user.service.api.IUserService;
import by.itacademy.user.service.api.UserRole;
import by.itacademy.user.service.api.ValidationError;
import by.itacademy.user.service.api.ValidationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService implements IUserService {

    private final UserDetailsManager manager;
    private final PasswordEncoder encoder;

    public UserService(UserDetailsManager manager,
                       PasswordEncoder encoder) {
        this.manager = manager;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void registration(LoginDto loginDto) {
        this.checkLoginDto(loginDto);

        String login = loginDto.getLogin();

        if (manager.userExists(login)) {
            throw new ValidationException("Логин недоступен для регистрации");
        }

        this.manager.createUser(User.builder()
                .username(login)
                .password(encoder.encode(loginDto.getPassword()))
                .roles(UserRole.USER.toString())
                .build());
    }

    @Override
    public String authorization(LoginDto loginDto) {
        String error = "Пароль или логин неверный (ые)";
        this.checkLoginDto(loginDto);

        UserDetails details;
        try {
            details = manager.loadUserByUsername(loginDto.getLogin());
        } catch (UsernameNotFoundException e) {
            throw new ValidationException(error);
        }

        if (!encoder.matches(loginDto.getPassword(), details.getPassword()) || !details.isEnabled()) {
            throw new ValidationException(error);
        }

        return JwtTokenUtil.generateAccessToken(details);
    }

    private void checkLoginDto(LoginDto loginDto) {
        if (loginDto == null) {
            throw new ValidationException("Не переданы логин и пароль");
        }

        List<ValidationError> errors = new ArrayList<>();

        if (isEmptyOrNull(loginDto.getLogin())) {
            errors.add(new ValidationError("login", "Не передан логин"));
        }

        if (isEmptyOrNull(loginDto.getPassword())) {
            errors.add(new ValidationError("password", "Не передан пароль"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private boolean isEmptyOrNull(String str) {
        return str == null || str.isEmpty();
    }
}
