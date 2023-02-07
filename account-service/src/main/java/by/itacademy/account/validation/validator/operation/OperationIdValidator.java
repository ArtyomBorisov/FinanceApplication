package by.itacademy.account.validation.validator.operation;

import by.itacademy.account.service.UserHolder;
import by.itacademy.account.validation.annotation.OperationExist;
import by.itacademy.account.validation.validator.helper.OperationValidatorHelper;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.UUID;

public class OperationIdValidator implements ConstraintValidator<OperationExist, UUID> {

    private final OperationValidatorHelper operationHelper;
    private final UserHolder userHolder;
    private final HttpServletRequest request;

    public OperationIdValidator(OperationValidatorHelper operationHelper,
                                UserHolder userHolder,
                                HttpServletRequest request) {
        this.operationHelper = operationHelper;
        this.userHolder = userHolder;
        this.request = request;
    }

    @Override
    public boolean isValid(UUID idOperation, ConstraintValidatorContext context) {
        if (idOperation == null) {
            return false;
        }

        String login = userHolder.getLoginFromContext();

//        получаю значение id из path_variable,
//        т.к. нужно проверить относится ли данная операция к счёту
        Map<String, String> map =
                (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String idAccountStr = map.get("uuid");
        UUID idAccount = UUID.fromString(idAccountStr);

        return operationHelper.isOperationIdExist(idOperation, idAccount, login, context);
    }
}
