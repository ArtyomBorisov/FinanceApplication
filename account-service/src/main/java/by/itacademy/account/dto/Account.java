package by.itacademy.account.dto;

import by.itacademy.account.constant.AccountType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonPropertyOrder({"uuid", "dt_create", "dt_update", "title", "description", "balance", "type", "currency"})
public class Account extends BaseDto {

    private String title;
    private String description;
    private double balance;
    private AccountType type;
    private UUID currency;

    @JsonIgnore
    private String user;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public UUID getCurrency() {
        return currency;
    }

    public void setCurrency(UUID currency) {
        this.currency = currency;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static class Builder {
        private final Account account;

        private Builder() {
            account = new Account();
        }

        public Builder setId(UUID id) {
            account.setId(id);
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            account.setDtCreate(dtCreate);
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            account.setDtUpdate(dtUpdate);
            return this;
        }

        public Builder setTitle(String title) {
            account.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            account.description = description;
            return this;
        }

        public Builder setBalance(double balance) {
            account.balance = balance;
            return this;
        }

        public Builder setType(AccountType accountType) {
            account.type = accountType;
            return this;
        }

        public Builder setCurrency(UUID currency) {
            account.currency = currency;
            return this;
        }

        public Builder setUser(String user) {
            account.user = user;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public Account build() {
            return account;
        }
    }
}
