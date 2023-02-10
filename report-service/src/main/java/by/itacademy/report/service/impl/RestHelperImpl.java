package by.itacademy.report.service.impl;

import by.itacademy.report.constant.UrlType;
import by.itacademy.report.dto.Account;
import by.itacademy.report.dto.Operation;
import by.itacademy.report.dto.Params;
import by.itacademy.report.service.RestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
        boolean lastPage = false;
        int page = -1;

        List<Operation> data = new ArrayList<>();

        while (!lastPage) {
            String pageJson = restTemplate.postForObject(
                    operationBackendUrl + "?page=" + ++page + "&size=20",
                    params,
                    String.class);

            Map<String, Object> pageMap = mapper.readValue(pageJson, Map.class);

            Object content = pageMap.get("content");

            Collection<Object> collection = mapper.convertValue(content, Collection.class);

            for (Object obj : collection) {
                Operation operation = mapper.convertValue(obj, Operation.class);
                data.add(operation);
            }

            lastPage = (boolean) pageMap.get("last");
        }

        return data;
    }

    @Override
    public List<Account> getAccounts() throws JsonProcessingException {
        List<Account> data = new ArrayList<>();

        boolean lastPage = false;
        int temp = -1;

        while (!lastPage) {
            String pageJson = restTemplate.getForObject(
                    accountUrl + "?page=" + ++temp + "&size=20",
                    String.class);

            Map<String, Object> pageMap = mapper.readValue(pageJson, Map.class);

            Object content = pageMap.get("content");

            Collection<Object> collection = mapper.convertValue(content, Collection.class);

            for (Object obj : collection) {
                Account account = mapper.convertValue(obj, Account.class);
                data.add(account);
            }

            lastPage = (boolean) pageMap.get("last");
        }

        return data;
    }

    @Override
    public List<Account> getAccounts(Set<UUID> uuids) throws JsonProcessingException {
        List<Account> data = new ArrayList<>();

        boolean lastPage = false;
        int temp = -1;

        while (!lastPage) {
            String pageJson = restTemplate.postForObject(
                    accountBackendUrl + "?page=" + ++temp + "&size=20",
                    uuids,
                    String.class);

            Map<String, Object> pageMap = mapper.readValue(pageJson, Map.class);

            Object content = pageMap.get("content");

            Collection<Object> collection = mapper.convertValue(content, Collection.class);

            for (Object obj : collection) {
                Account account = mapper.convertValue(obj, Account.class);
                data.add(account);
            }

            lastPage = (boolean) pageMap.get("last");
        }

        return data;
    }

    @Override
    public Map<UUID, String> getTitles(Set<UUID> collection, UrlType url) throws JsonProcessingException {
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

        Map<UUID, String> data = new HashMap<>();
        boolean lastPage = false;
        int page = -1;

        while (!lastPage) {
            String pageJson = restTemplate.postForObject(
                    finalUrl + "?page=" + ++page + "&size=20",
                    collection,
                    String.class);

            Map<String, Object> pageMap = mapper.readValue(pageJson, Map.class);

            List<Map<String, Object>> content = (List<Map<String, Object>>) pageMap.get("content");

            for (Map<String, Object> map : content) {
                String temp = (String) map.get("uuid");
                UUID uuid = UUID.fromString(temp);
                String title = (String) map.get("title");
                data.put(uuid, title);
            }

            lastPage = (boolean) pageMap.get("last");
        }

        return data;
    }
}
