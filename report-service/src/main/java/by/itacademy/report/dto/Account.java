package by.itacademy.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Account {
    @JsonProperty("uuid")
    private UUID id;
    private String title;
    private double balance;
    private UUID currency;
    private String type;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getCurrency() {
        return currency;
    }

    public void setCurrency(UUID currency) {
        this.currency = currency;
    }
}
