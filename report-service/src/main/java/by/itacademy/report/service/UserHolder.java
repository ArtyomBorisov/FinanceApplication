package by.itacademy.report.service;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserHolder {
    public UserDetails getUser(){
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public String getLoginFromContext() {
        UserDetails user = getUser();

        if (user == null) {
            throw new SecurityException("Ошибка безопасности");
        }

        return user.getUsername();
    }
}
