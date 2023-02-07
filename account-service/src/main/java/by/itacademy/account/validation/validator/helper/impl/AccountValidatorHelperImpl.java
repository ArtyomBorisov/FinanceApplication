package by.itacademy.account.validation.validator.helper.impl;

import by.itacademy.account.constant.AccountType;
import by.itacademy.account.constant.FieldName;
import by.itacademy.account.constant.MessageError;
import by.itacademy.account.repository.AccountRepository;
import by.itacademy.account.validation.validator.helper.AccountValidatorHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class AccountValidatorHelperImpl implements AccountValidatorHelper {

    private final AccountRepository repository;

    public AccountValidatorHelperImpl(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isAccountTypeValid(AccountType accountType, ConstraintValidatorContext context) {
        if (accountType == null) {
            addConstraintViolation(MessageError.MISSING_FIELD, FieldName.TYPE, context);
            return false;
        }
        return true;
    }

    @Override
    public boolean isTitleValid(String title, String login, ConstraintValidatorContext context) {
        boolean valid = true;

        if (title == null || title.isEmpty()) {
            addConstraintViolation(MessageError.MISSING_FIELD, FieldName.TITLE, context);
            valid = false;

        } else if (repository.findByUserAndTitle(login, title).isPresent()) {
            addConstraintViolation(MessageError.NOT_UNIQUE_FIELD, FieldName.TITLE, context);
            valid = false;
        }

        return valid;
    }

    @Override
    public boolean isAccountIdExist(UUID accountUuid, String login, ConstraintValidatorContext context) {
        if (accountUuid == null) {
            return false;
        }

        if (!repository.existsAccountEntityByUserAndId(login, accountUuid)) {
            addConstraintViolation(MessageError.ID_NOT_EXIST, accountUuid.toString(), context);
            return false;
        }

        return true;
    }

    @Override
    public boolean titleEqualTo(String title, String login, UUID accountUuid) {
        if(accountUuid != null && title != null) {
            return repository
                    .findByUserAndId(login, accountUuid)
                    .stream()
                    .allMatch(entity -> Objects.equals(title, entity.getTitle()));
        }
        return false;
    }

    private void addConstraintViolation(String message, String name, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(name)
                .addConstraintViolation();
    }
}























