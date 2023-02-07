package by.itacademy.account.validation.validator.helper.impl;

import by.itacademy.account.constant.FieldName;
import by.itacademy.account.constant.MessageError;
import by.itacademy.account.exception.ServerException;
import by.itacademy.account.validation.validator.helper.ClassifierValidatorHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

@Component
public class ClassifierValidatorHelperImpl implements ClassifierValidatorHelper {

    private final String currencyUrl;
    private final String categoryUrl;
    private final RestTemplate restTemplate;

    public ClassifierValidatorHelperImpl(@Value("${classifier_currency_url}") String currencyUrl,
                                         @Value("${classifier_category_url}") String categoryUrl,
                                         RestTemplate restTemplate) {
        this.currencyUrl = currencyUrl;
        this.categoryUrl = categoryUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isCurrencyIdExist(UUID idCurrency, ConstraintValidatorContext context) {
        boolean valid = true;

        if (idCurrency == null) {
            addConstraintViolation(MessageError.MISSING_FIELD, FieldName.ID_CURRENCY, context);
            valid = false;
        } else if (!checkId(currencyUrl, idCurrency)) {
            addConstraintViolation(MessageError.ID_NOT_EXIST, FieldName.ID_CURRENCY, context);
            valid = false;
        }

        return valid;
    }

    @Override
    public boolean isCategoryIdExist(UUID idCategory, ConstraintValidatorContext context) {
        boolean valid = true;

        if (idCategory == null) {
            addConstraintViolation(MessageError.MISSING_FIELD, FieldName.ID_CATEGORY, context);
            valid = false;
        } else if (!checkId(categoryUrl, idCategory)) {
            addConstraintViolation(MessageError.ID_NOT_EXIST, FieldName.ID_CATEGORY, context);
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

    private boolean checkId(String url, UUID id) throws ServerException {
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
