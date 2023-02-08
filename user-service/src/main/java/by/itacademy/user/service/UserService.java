package by.itacademy.user.service;

import by.itacademy.user.dto.LoginDto;

public interface UserService {
    void registration(LoginDto loginDto);
    String authorization(LoginDto loginDt);
}
