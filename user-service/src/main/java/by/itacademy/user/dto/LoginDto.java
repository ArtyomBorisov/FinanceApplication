package by.itacademy.user.dto;

public class LoginDto {
    private String login;
    private String password;

    public LoginDto() {
    }

    public LoginDto(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
