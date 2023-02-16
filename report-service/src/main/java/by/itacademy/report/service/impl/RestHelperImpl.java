package by.itacademy.report.service.impl;

import by.itacademy.report.constant.MessageError;
import by.itacademy.report.constant.UrlType;
import by.itacademy.report.dto.Account;
import by.itacademy.report.dto.Operation;
import by.itacademy.report.dto.Params;
import by.itacademy.report.exception.ServerException;
import by.itacademy.report.service.RestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RestHelperImpl implements RestHelper {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String operationBackendUrl;
    private final String accountBackendUrl;
    private final String currencyBackendUrl;
    private final String categoryBackendUrl;
    private final String accountUrl;

    public RestHelperImpl(RestTemplate restTemplate,
                          ObjectMapper mapper,
                          @Value("${operation_backend_url}") String operationBackendUrl,
                          @Value("${account_backend_url}") String accountBackendUrl,
                          @Value("${currency_backend_url}") String currencyBackendUrl,
                          @Value("${category_backend_url}") String categoryBackendUrl,
                          @Value("${account_url}") String accountUrl) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.operationBackendUrl = operationBackendUrl;
        this.accountBackendUrl = accountBackendUrl;
        this.currencyBackendUrl = currencyBackendUrl;
        this.categoryBackendUrl = categoryBackendUrl;
        this.accountUrl = accountUrl;
    }

    @Override
    public List<Operation> getOperations(Params params) throws JsonProcessingException {
        List<Object> content = getContent(operationBackendUrl, Method.POST, params);

        return content.stream()
                .map(obj -> mapper.convertValue(obj, Operation.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> getAccounts() throws JsonProcessingException {
        List<Object> content = getContent(accountUrl, Method.GET, null);

        return content.stream()
                .map(obj -> mapper.convertValue(obj, Account.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> getAccounts(Set<UUID> uuids) throws JsonProcessingException {
        List<Object> content = getContent(accountUrl, Method.POST, uuids);
        return content.stream()
                .map(obj -> mapper.convertValue(obj, Account.class))
                .collect(Collectors.toList());
    }

    @Override
    public Map<UUID, String> getTitles(Set<UUID> uuids, UrlType url) throws JsonProcessingException {
        String finalUrl = null;

        switch (url) {
            case ACCOUNT:
                finalUrl = accountBackendUrl;
                break;
            case OPERATION:
                finalUrl = operationBackendUrl;
                break;
            case CURRENCY:
                finalUrl = currencyBackendUrl;
                break;
            case CATEGORY:
                finalUrl = categoryBackendUrl;
                break;
        }

        List<Object> content = getContent(finalUrl, Method.POST, uuids);

        Map<UUID, String> data = new HashMap<>();
        for (Object obj : content) {
            Map<String, Object> map = (Map<String, Object>) obj;
            String uuidStr = (String) map.get("uuid");
            UUID uuid = UUID.fromString(uuidStr);
            String title = (String) map.get("title");
            data.put(uuid, title);
        }
        return data;
    }

    private List<Object> getContent(String url, Method method, Object body) throws JsonProcessingException {
        List<Object> data = new ArrayList<>();

        boolean lastPage = false;
        int page = -1;

        while (!lastPage) {
            String pageJson;
            if (method == Method.GET) {
                pageJson = restTemplate.getForObject(
                        url + "?page=" + ++page + "&size=20",
                        String.class);
            } else if (method == Method.POST) {
                pageJson = restTemplate.postForObject(
                        url + "?page=" + ++page + "&size=20",
                        body,
                        String.class);
            } else {
                throw new ServerException(MessageError.NO_REALIZATION_FOR_METHOD);
            }

            Map<String, Object> pageMap = mapper.readValue(pageJson, Map.class);
            Object content = pageMap.get("content");
            Collection<Object> collection = mapper.convertValue(content, Collection.class);
            data.addAll(collection);

            lastPage = (boolean) pageMap.get("last");
        }

        return data;
    }

    private enum Method {
        GET,
        POST
    }
}
