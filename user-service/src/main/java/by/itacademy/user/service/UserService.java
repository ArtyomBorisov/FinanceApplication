package by.itacademy.user.service;

import by.itacademy.user.controller.utils.JwtTokenUtil;
import by.itacademy.user.service.api.IUserService;
import by.itacademy.user.service.api.UserRole;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void registration(String login, String password) {
//        добавить проверки login password на null
        if (manager.userExists(login)) {
            throw new IllegalArgumentException("Логин существует");
        }

        this.manager.createUser(User.builder()
                .username(login)
                .password(encoder.encode(password))
                .roles(UserRole.USER.toString())
                .build());
    }

    @Override
    public String authorization(String login, String password) {
//        добавить проверки login password на null

        UserDetails details = manager.loadUserByUsername(login);

        if (!encoder.matches(password, details.getPassword())) {
            throw new IllegalArgumentException("Пароль неверный");
        } else if (!details.isEnabled()) {
            throw new IllegalArgumentException("Пользователь удалён");
        }

        return JwtTokenUtil.generateAccessToken(details);
    }

    @Override
    public boolean isEnabled(String login, String password) {
//        добавить проверки login password на null

        UserDetails details = manager.loadUserByUsername(login);

        if (!encoder.matches(password, details.getPassword())) {
            throw new IllegalArgumentException("Пароль неверный");
        } else if (!details.isEnabled()) {
            throw new IllegalArgumentException("Пользователь удалён");
        }

        return true;
    }
}
