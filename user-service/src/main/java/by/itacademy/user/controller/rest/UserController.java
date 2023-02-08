package by.itacademy.user.controller.rest;

import by.itacademy.user.dto.LoginDto;
import by.itacademy.user.service.UserService;
import by.itacademy.user.validation.annotation.AuthValid;
import by.itacademy.user.validation.annotation.CustomValid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/user")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/reg")
    @ResponseStatus(HttpStatus.CREATED)
    public void registration(@RequestBody @CustomValid LoginDto loginDto){
        userService.registration(loginDto);
    }

    @PostMapping("/login")
    public String login(@RequestBody @AuthValid LoginDto loginDto){
        return userService.authorization(loginDto);
    }
}
