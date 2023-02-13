package by.itacademy.account.validation.validator.operation;

import by.itacademy.account.constant.FieldName;
import by.itacademy.account.constant.MessageError;
import by.itacademy.account.constant.ParamSort;
import by.itacademy.account.dto.Params;
import by.itacademy.account.service.UserHolder;
import by.itacademy.account.validation.annotation.CustomValid;
import by.itacademy.account.validation.validator.helper.AccountValidatorHelper;
import by.itacademy.account.validation.validator.helper.ClassifierValidatorHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.UUID;

public class ParamsValidator implements ConstraintValidator<CustomValid, Params> {

    private final AccountValidatorHelper accountHelper;
    private final ClassifierValidatorHelper classifierHelper;
    private final UserHolder userHolder;

    public ParamsValidator(AccountValidatorHelper accountHelper,
                           ClassifierValidatorHelper classifierHelper,
                           UserHolder userHolder) {
        this.accountHelper = accountHelper;
        this.classifierHelper = classifierHelper;
        this.userHolder = userHolder;
    }

    @Override
    public boolean isValid(Params params, ConstraintValidatorContext context) {
        String login = userHolder.getLoginFromContext();
        Set<UUID> accounts = params.getAccounts();
        Set<UUID> categories = params.getCategories();
        ParamSort sort = params.getSort();

        boolean valid = true;

        if (accounts != null) {
            for (UUID account : accounts) {
                if (!accountHelper.isAccountIdExist(account, login, context)) valid = false;
            }
        }

        if (categories != null) {
            for (UUID category : categories) {
                if (!classifierHelper.isCategoryIdExist(category, context)) valid = false;
            }
        }

        if (sort == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MessageError.MISSING_FIELD)
                    .addPropertyNode(FieldName.SORT)
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
