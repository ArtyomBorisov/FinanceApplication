package by.itacademy.report.dto;

import by.itacademy.report.constant.ReportType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@JsonPropertyOrder({"accounts", "categories", "from", "to"})
public class Params {
    private Set<UUID> accounts;
    private Set<UUID> categories;
    private LocalDate from;
    private LocalDate to;

    @JsonIgnore
    private ReportType sort;

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

    public ReportType getSort() {
        return sort;
    }

    public void setSort(ReportType sort) {
        this.sort = sort;
    }
}
