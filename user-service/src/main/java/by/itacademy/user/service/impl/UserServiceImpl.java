package by.itacademy.user.service.impl;

import by.itacademy.user.dto.LoginDto;
import by.itacademy.user.service.UserService;
import by.itacademy.user.utils.JwtTokenUtil;
import by.itacademy.user.constant.UserRole;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserDetailsManager manager;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserDetailsManager manager,
                           PasswordEncoder encoder) {
        this.manager = manager;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void registration(LoginDto loginDto) {
        String login = loginDto.getLogin();
        String password = loginDto.getPassword();

        manager.createUser(User.builder()
                .username(login)
                .password(encoder.encode(password))
                .roles(UserRole.USER.toString())
                .build());
    }

    @Override
    public String authorization(LoginDto loginDto) {
        UserDetails details = manager.loadUserByUsername(loginDto.getLogin());

        if (encoder.matches(loginDto.getPassword(), details.getPassword()) && details.isEnabled()) {
            return JwtTokenUtil.generateAccessToken(details);
        }

        return null;
    }
}
