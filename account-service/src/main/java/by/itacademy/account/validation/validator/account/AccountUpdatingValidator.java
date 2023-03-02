package by.itacademy.account.validation.validator.account;

import by.itacademy.account.dto.Account;
import by.itacademy.account.service.UserHolder;
import by.itacademy.account.validation.annotation.AccountUpdatingValid;
import by.itacademy.account.validation.validator.helper.AccountValidatorHelper;
import by.itacademy.account.validation.validator.helper.ClassifierValidatorHelper;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.UUID;

public class AccountUpdatingValidator implements ConstraintValidator<AccountUpdatingValid, Account> {

    private final AccountValidatorHelper accountHelper;
    private final ClassifierValidatorHelper classifierHelper;
    private final UserHolder userHolder;
    private final HttpServletRequest request;

    public AccountUpdatingValidator(AccountValidatorHelper accountHelper,
                                    ClassifierValidatorHelper classifierHelper,
                                    UserHolder userHolder,
                                    HttpServletRequest request) {
        this.accountHelper = accountHelper;
        this.classifierHelper = classifierHelper;
        this.userHolder = userHolder;
        this.request = request;
    }

    @Override
    public boolean isValid(Account account, ConstraintValidatorContext context) {
        if (account == null) {
            return false;
        }

        String login = userHolder.getLoginFromContext();
        UUID id = getIdFromPathVariable();

        boolean typeValid = accountHelper.isAccountTypeValid(account.getType(), context);
        boolean currencyIdValid = classifierHelper.isCurrencyIdExist(account.getCurrency(), context);
        boolean accountIdValid = accountHelper.isAccountIdExist(id, login, context);

        String newTitle = account.getTitle();

        boolean titleValid = true;
        if (accountIdValid && !accountHelper.titleEqualTo(newTitle, login, id)) {
            titleValid = accountHelper.isTitleValid(newTitle, login, context);
        }

        return typeValid && currencyIdValid && accountIdValid && titleValid;
    }

    private UUID getIdFromPathVariable() {
        Map<String, String> map =
                (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String idStr = map.get("uuid");
        return UUID.fromString(idStr);
    }
}
