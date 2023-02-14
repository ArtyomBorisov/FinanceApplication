package by.itacademy.classifier.service.impl;

import by.itacademy.classifier.service.UserHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class UserHolderImpl implements UserHolder {

    @Override
    public String getLoginFromContext() {
        UserDetails user = getUser();

        if (user == null) {
            throw new SecurityException("Ошибка безопасности");
        }

        return user.getUsername();
    }

    @Override
    public boolean isAdmin() {
        UserDetails user = getUser();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return authorities.stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
    }

    private UserDetails getUser() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
