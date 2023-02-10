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

    public static class Builder {
        private final Account account;

        private Builder() {
            this.account = new Account();
        }

        public Builder setId(UUID id) {
            this.account.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.account.title = title;
            return this;
        }

        public Builder setBalance(double balance) {
            this.account.balance = balance;
            return this;
        }

        public Builder setType(String accountType) {
            this.account.type = accountType;
            return this;
        }

        public Builder setCurrency(UUID currency) {
            this.account.currency = currency;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public Account build() {
            return this.account;
        }
    }
}
