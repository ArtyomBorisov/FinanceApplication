package by.itacademy.mail.service.impl;

import by.itacademy.mail.service.UserHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserHolderImpl implements UserHolder {

    @Override
    public String getLoginFromContext() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            throw new SecurityException("Ошибка безопасности");
        }

        return user.getUsername();
    }
}
