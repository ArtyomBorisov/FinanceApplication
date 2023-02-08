package by.itacademy.account.dto;

import by.itacademy.account.constant.ParamSort;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public class Params {
    private Set<UUID> accounts;
    private Set<UUID> categories;
    private LocalDate from;
    private LocalDate to;
    private ParamSort sort;

    public Set<UUID> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<UUID> accounts) {
        this.accounts = accounts;
    }

    public Set<UUID> getCategories() {
        return categories;
    }

    public void setCategories(Set<UUID> categories) {
        this.categories = categories;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public ParamSort getSort() {
        return sort;
    }

    public void setSort(ParamSort sort) {
        this.sort = sort;
    }
}
