package by.itacademy.account.validation.validator.helper;

import by.itacademy.account.constant.AccountType;

import javax.validation.ConstraintValidatorContext;import java.util.UUID;

public interface AccountValidatorHelper {
    boolean isAccountTypeValid(AccountType accountType, ConstraintValidatorContext context);

    boolean isTitleValid(String title, String login, ConstraintValidatorContext context);

    boolean isAccountIdExist(UUID accountUuid, String login, ConstraintValidatorContext context);

    boolean titleEqualTo(String title, String login, UUID accountUuid);
}
