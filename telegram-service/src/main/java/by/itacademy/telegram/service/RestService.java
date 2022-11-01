package by.itacademy.telegram.service;

import by.itacademy.telegram.dto.LoginDto;

public interface RestService {
    String signIn(LoginDto loginDto);
}
