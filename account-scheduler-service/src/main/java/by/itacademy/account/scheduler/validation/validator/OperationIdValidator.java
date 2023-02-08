package by.itacademy.account.scheduler.validation.validator;

import by.itacademy.account.scheduler.constant.MessageError;
import by.itacademy.account.scheduler.dao.ScheduledOperationRepository;
import by.itacademy.account.scheduler.service.UserHolder;
import by.itacademy.account.scheduler.validation.annotation.Exist;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

@Transactional(readOnly = true)
public class OperationIdValidator implements ConstraintValidator<Exist, UUID> {

    private final UserHolder userHolder;
    private final ScheduledOperationRepository repository;

    public OperationIdValidator(UserHolder userHolder,
                                ScheduledOperationRepository repository) {
        this.userHolder = userHolder;
        this.repository = repository;
    }

    @Override
    public boolean isValid(UUID id, ConstraintValidatorContext context) {
        if (id == null) {
            return false;
        }

        String login = userHolder.getLoginFromContext();

        if (repository.findByUserAndId(login, id).isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MessageError.ID_NOT_EXIST)
                    .addPropertyNode(id.toString())
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
