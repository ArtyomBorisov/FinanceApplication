package by.itacademy.account.validation.validator.account;

import by.itacademy.account.service.UserHolder;
import by.itacademy.account.validation.annotation.AccountExist;
import by.itacademy.account.validation.validator.helper.AccountValidatorHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

public class AccountIdValidator implements ConstraintValidator<AccountExist, UUID> {

    private final AccountValidatorHelper helper;
    private final UserHolder userHolder;

    public AccountIdValidator(AccountValidatorHelper helper,
                              UserHolder userHolder) {
        this.helper = helper;
        this.userHolder = userHolder;
    }

    @Override
    public boolean isValid(UUID uuid, ConstraintValidatorContext context) {
        String login = userHolder.getLoginFromContext();

        return helper.isAccountIdExist(uuid, login, context);
    }
}
