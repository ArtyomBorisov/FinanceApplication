package by.itacademy.user.service.api;

public interface IUserService {
    void registration(String login, String password);
    String authorization(String login, String password);
    boolean isEnabled(String login, String password);
}
