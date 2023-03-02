package by.itacademy.account.validation.validator.helper.impl;

import by.itacademy.account.exception.ServerException;
import by.itacademy.account.validation.validator.helper.ClassifierValidatorHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

import static by.itacademy.account.constant.FieldName.ID_CATEGORY;
import static by.itacademy.account.constant.FieldName.ID_CURRENCY;
import static by.itacademy.account.constant.MessageError.ID_NOT_EXIST;
import static by.itacademy.account.constant.MessageError.MISSING_FIELD;

@Component
public class ClassifierValidatorHelperImpl implements ClassifierValidatorHelper {

    private final String currencyUrl;
    private final String categoryUrl;
    private final RestTemplate restTemplate;

    public ClassifierValidatorHelperImpl(@Value("${urls.currency_backend}") String currencyUrl,
                                         @Value("${urls.category_backend}") String categoryUrl,
                                         RestTemplate restTemplate) {
        this.currencyUrl = currencyUrl;
        this.categoryUrl = categoryUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isCurrencyIdExist(UUID idCurrency, ConstraintValidatorContext context) {
        if (idCurrency == null) {
            addConstraintViolation(MISSING_FIELD, ID_CURRENCY, context);
            return false;
        }

        if (!isIdValid(currencyUrl, idCurrency)) {
            addConstraintViolation(ID_NOT_EXIST, ID_CURRENCY, context);
            return false;
        }

        return true;
    }

    @Override
    public boolean isCategoryIdExist(UUID idCategory, ConstraintValidatorContext context) {
        if (idCategory == null) {
            addConstraintViolation(MISSING_FIELD, ID_CATEGORY, context);
            return false;
        }

        if (!isIdValid(categoryUrl, idCategory)) {
            addConstraintViolation(ID_NOT_EXIST, ID_CATEGORY, context);
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

    private boolean isIdValid(String url, UUID id) throws ServerException {
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
