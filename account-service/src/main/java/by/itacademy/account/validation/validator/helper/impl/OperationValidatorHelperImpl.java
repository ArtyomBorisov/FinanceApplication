package by.itacademy.account.validation.validator.helper.impl;

import by.itacademy.account.constant.FieldName;
import by.itacademy.account.constant.MessageError;
import by.itacademy.account.dao.OperationRepository;
import by.itacademy.account.dao.entity.AccountEntity;
import by.itacademy.account.dao.entity.OperationEntity;
import by.itacademy.account.validation.validator.helper.OperationValidatorHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
public class OperationValidatorHelperImpl implements OperationValidatorHelper {

    private final OperationRepository repository;

    public OperationValidatorHelperImpl(OperationRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isValueValid(double value, ConstraintValidatorContext context) {
        if (value == 0) {
            addConstraintViolation(MessageError.INCORRECT_OPERATION_VALUE, FieldName.VALUE, context);
            return false;
        }
        return true;
    }

    @Override
    public boolean isOperationIdExist(UUID idOperation,
                                      UUID idAccount,
                                      String userLogin,
                                      ConstraintValidatorContext context) {
        if (idAccount == null || idOperation == null || userLogin == null) {
            return false;
        }

        Optional<OperationEntity> optional = repository.findById(idOperation);

        if (optional.isEmpty()) {
            addConstraintViolation(MessageError.ID_NOT_EXIST, FieldName.ID_OPERATION, context);
            return false;
        }

        AccountEntity account = optional.get().getAccountEntity();

        UUID idAccountFromDB = account.getId();
        String userLoginFromDB = account.getUser();

        if (idAccount.compareTo(idAccountFromDB) != 0
                || userLogin.compareTo(userLoginFromDB) != 0) {
            addConstraintViolation(MessageError.ID_NOT_EXIST, FieldName.ID_ACCOUNT, context);
            return false;
        }

        return true;
    }

    private void addConstraintViolation(String message, String name, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(name)
                .addConstraintViolation();
    }
}
