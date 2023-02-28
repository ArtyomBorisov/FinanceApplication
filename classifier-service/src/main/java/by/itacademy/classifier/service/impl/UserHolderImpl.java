package by.itacademy.classifier.service.impl;

import by.itacademy.classifier.service.UserHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static by.itacademy.classifier.constant.MessageError.SECURITY_EXCEPTION;

@Component
public class UserHolderImpl implements UserHolder {
    @Override
    public String getLoginFromContext() {
        return getOptionalUser()
                .map(UserDetails::getUsername)
                .orElseThrow(() -> new SecurityException(SECURITY_EXCEPTION));
    }

    @Override
    public boolean isAdmin() {
        UserDetails user = getOptionalUser()
                .orElseThrow(() -> new SecurityException(SECURITY_EXCEPTION));

        return user.getAuthorities()
                .stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
    }

    private Optional<UserDetails> getOptionalUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Optional.ofNullable(userDetails);
    }
}
