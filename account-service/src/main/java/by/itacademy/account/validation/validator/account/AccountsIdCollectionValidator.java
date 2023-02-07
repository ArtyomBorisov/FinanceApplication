package by.itacademy.account.validation.validator.account;

import by.itacademy.account.service.UserHolder;
import by.itacademy.account.validation.annotation.AccountExist;
import by.itacademy.account.validation.validator.helper.AccountValidatorHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.UUID;

public class AccountsIdCollectionValidator implements ConstraintValidator<AccountExist, Collection<UUID>> {

    private final AccountValidatorHelper helper;
    private final UserHolder userHolder;

    public AccountsIdCollectionValidator(AccountValidatorHelper helper,
                                         UserHolder userHolder) {
        this.helper = helper;
        this.userHolder = userHolder;
    }

    @Override
    public boolean isValid(Collection<UUID> collection, ConstraintValidatorContext context) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }

        String login = userHolder.getLoginFromContext();
        boolean valid = true;
        for (UUID uuid : collection) {
            if (!helper.isAccountIdExist(uuid, login, context)) {
                valid = false;
            }
        }

        return valid;
    }
}
