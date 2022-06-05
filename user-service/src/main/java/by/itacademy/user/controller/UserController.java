package by.itacademy.user.controller;

import by.itacademy.user.controller.dto.LoginDto;
import by.itacademy.user.service.api.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/reg")
    @ResponseStatus(HttpStatus.CREATED)
    public void registration(@RequestBody LoginDto loginDto){
        this.userService.registration(loginDto);
    }

    @PostMapping(value = "/login")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public String login(@RequestBody LoginDto loginDto){
        return this.userService.authorization(loginDto);
    }
}
