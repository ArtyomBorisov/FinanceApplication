package by.itacademy.user.service.api;

import by.itacademy.user.controller.dto.LoginDto;

public interface IUserService {
    void registration(LoginDto loginDto);
    String authorization(LoginDto loginDt);
}
