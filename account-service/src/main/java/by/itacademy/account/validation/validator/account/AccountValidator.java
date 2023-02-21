package by.itacademy.account.validation.validator.account;

import by.itacademy.account.dto.Account;
import by.itacademy.account.service.UserHolder;
import by.itacademy.account.validation.annotation.CustomValid;
import by.itacademy.account.validation.validator.helper.AccountValidatorHelper;
import by.itacademy.account.validation.validator.helper.ClassifierValidatorHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountValidator implements ConstraintValidator<CustomValid, Account> {

    private final AccountValidatorHelper accountHelper;
    private final ClassifierValidatorHelper classifierHelper;
    private final UserHolder userHolder;

    public AccountValidator(AccountValidatorHelper accountHelper,
                            ClassifierValidatorHelper classifierHelper,
                            UserHolder userHolder) {
        this.accountHelper = accountHelper;
        this.classifierHelper = classifierHelper;
        this.userHolder = userHolder;
    }

    @Override
    public boolean isValid(Account account, ConstraintValidatorContext context) {
        if (account == null) {
            return false;
        }

        String login = userHolder.getLoginFromContext();

        boolean accountTypeValid = accountHelper.isAccountTypeValid(account.getType(), context);
        boolean currencyIdExist = classifierHelper.isCurrencyIdExist(account.getCurrency(), context);
        boolean titleValid = accountHelper.isTitleValid(account.getTitle(), login, context);

        return accountTypeValid && currencyIdExist && titleValid;
    }


}
