package by.itacademy.report.service;

import by.itacademy.report.constant.UrlType;
import by.itacademy.report.dto.Account;
import by.itacademy.report.dto.Operation;
import by.itacademy.report.dto.Params;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.*;

public interface RestHelper {
    List<Operation> getOperations(Params params) throws JsonProcessingException;

    List<Account> getAccounts() throws JsonProcessingException;

    List<Account> getAccounts(Set<UUID> uuids) throws JsonProcessingException;

    Map<UUID, String> getTitles(Set<UUID> collection, UrlType url) throws JsonProcessingException;
}
