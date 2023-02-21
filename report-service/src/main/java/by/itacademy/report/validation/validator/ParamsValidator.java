package by.itacademy.report.validation.validator;

import by.itacademy.report.constant.MessageError;
import by.itacademy.report.constant.ReportType;
import by.itacademy.report.dto.Params;
import by.itacademy.report.exception.ServerException;
import by.itacademy.report.validation.annotation.CustomValid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ParamsValidator implements ConstraintValidator<CustomValid, Params> {

    private final HttpServletRequest request;
    private final RestTemplate restTemplate;
    private final String accountUrl;
    private final String categoryUrl;

    public ParamsValidator(HttpServletRequest request,
                           RestTemplate restTemplate,
                           @Value("${urls.account}") String accountUrl,
                           @Value("${urls.currency_backend}") String categoryUrl) {
        this.request = request;
        this.restTemplate = restTemplate;
        this.accountUrl = accountUrl;
        this.categoryUrl = categoryUrl;
    }

    @Override
    public boolean isValid(Params params, ConstraintValidatorContext context) {
        if (params == null) {
            return false;
        }

//        получаю значение report type из path_variable,
//        т.к. нужно понять какие параметры должны быть переданы
        Map<String, String> map =
                (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String type = map.get("type");

        ReportType sort = ReportType.valueOf(type);

        if (sort == ReportType.BALANCE) {
            return checkParamsForBalanceReport(params, context);
        } else if (sort == ReportType.BY_CATEGORY || sort == ReportType.BY_DATE) {
            return checkParamsForOperationReport(params, context);
        } else {
            throw new ServerException("Нет реализации такого отчёта");
        }
    }

    private boolean checkParamsForBalanceReport(Params params, ConstraintValidatorContext context) {
        Set<UUID> accounts = params.getAccounts();
        return check(accounts, accountUrl, context);
    }

    private boolean checkParamsForOperationReport(Params params, ConstraintValidatorContext context) {
        Set<UUID> accounts = params.getAccounts();
        Set<UUID> categories = params.getCategories();

        boolean accountsValid = check(accounts, accountUrl, context);
        boolean categoriesValid = check(categories, categoryUrl, context);

        return accountsValid && categoriesValid;
    }

    private boolean check(Collection<UUID> uuids, String url, ConstraintValidatorContext context) {
        if (uuids == null || uuids.isEmpty()) {
            return true;
        }

        boolean valid = true;

        for (UUID uuid : uuids) {
            try {
                String urlWithId = url + "/" + uuid;
                restTemplate.getForObject(urlWithId, String.class);
            } catch (HttpStatusCodeException e) {
                if (e.getStatusCode().is4xxClientError()) {
                    addConstraintViolation(MessageError.ID_NOT_EXIST, uuid.toString(), context);
                    valid = false;
                } else {
                    throw new ServerException(e);
                }
            }
        }

        return valid;
    }

    private void addConstraintViolation(String message, String name, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(name)
                .addConstraintViolation();
    }
}
