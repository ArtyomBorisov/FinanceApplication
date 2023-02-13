package by.itacademy.account.scheduler.validation.validator;

import by.itacademy.account.scheduler.dto.Operation;
import by.itacademy.account.scheduler.dto.Schedule;
import by.itacademy.account.scheduler.dto.ScheduledOperation;
import by.itacademy.account.scheduler.exception.ServerException;
import by.itacademy.account.scheduler.validation.annotation.CustomValid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.UUID;

import static by.itacademy.account.scheduler.constant.FieldName.*;
import static by.itacademy.account.scheduler.constant.MessageError.*;

public class ScheduledOperationValidator implements ConstraintValidator<CustomValid, ScheduledOperation> {

    private final RestTemplate restTemplate;
    private final String accountUrl;
    private final String currencyUrl;
    private final String categoryUrl;

    public ScheduledOperationValidator(RestTemplate restTemplate,
                                       @Value("${account_url}") String accountUrl,
                                       @Value("${classifier_currency_url}") String currencyUrl,
                                       @Value("${classifier_category_url}") String categoryUrl) {
        this.restTemplate = restTemplate;
        this.accountUrl = accountUrl;
        this.currencyUrl = currencyUrl;
        this.categoryUrl = categoryUrl;
    }

    @Override
    public boolean isValid(ScheduledOperation scheduledOperation, ConstraintValidatorContext context) {
        if (scheduledOperation == null) {
            return false;
        }

        Operation operation = scheduledOperation.getOperation();
        Schedule schedule = scheduledOperation.getSchedule();

        boolean operationValid = isOperationValid(operation, context);
        boolean scheduleValid = isScheduleValid(schedule, context);

        return operationValid && scheduleValid;
    }

    private boolean isOperationValid(Operation operation, ConstraintValidatorContext context) {
        if (operation == null) {
            addConstraintViolation(MISSING_FIELD, OPERATION, context);
            return false;
        }

        UUID idAccount = operation.getAccount();
        UUID idCategory = operation.getCategory();
        UUID idCurrency = operation.getCurrency();

        boolean accountValid = checkId(idAccount, accountUrl, ID_ACCOUNT, context);
        boolean categoryValid = checkId(idCategory, categoryUrl, ID_CATEGORY, context);
        boolean currencyValid = checkId(idCurrency, currencyUrl, ID_CURRENCY, context);

        boolean valueValid = true;
        if (operation.getValue() == 0) {
            addConstraintViolation(INCORRECT_OPERATION_VALUE, VALUE, context);
            valueValid = false;
        }

        return accountValid && categoryValid && currencyValid && valueValid;
    }

    private boolean isScheduleValid(Schedule schedule, ConstraintValidatorContext context) {
        if (schedule == null) {
            addConstraintViolation(MISSING_FIELD, SCHEDULE, context);
            return false;
        }

        boolean valid = true;

        long interval = schedule.getInterval();
        if (interval < 0) {
            addConstraintViolation(INVALID_INTERVAL, INTERVAL, context);
            valid = false;
        }

        if (interval > 0 && schedule.getTimeUnit() == null) {
            addConstraintViolation(MISSING_FIELD, TIME_UNIT, context);
            valid = false;
        }

        LocalDateTime start = schedule.getStartTime();
        LocalDateTime stop = schedule.getStopTime();
        if (stop != null && start != null && start.isAfter(stop)) {
            addConstraintViolation(INCORRECT_DATES, DATES, context);
            valid = false;
        }

        return valid;
    }



    private boolean checkId(UUID id, String url, String field, ConstraintValidatorContext context) {
        boolean valid = true;

        if (id == null) {
            addConstraintViolation(MISSING_FIELD, field, context);
            valid = false;
        } else if (!isObjectExist(url, id)) {
            addConstraintViolation(ID_NOT_EXIST, field, context);
            valid = false;
        }

        return valid;
    }

    private void addConstraintViolation(String message, String name, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(name)
                .addConstraintViolation();
    }

    private boolean isObjectExist(String url, UUID id) {
        String urlWithId = url + "/" + id;

        try {
            restTemplate.getForObject(urlWithId, String.class);
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return false;
            } else {
                throw new ServerException(e);
            }
        }

        return true;
    }
}
