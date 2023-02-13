package by.itacademy.account.scheduler.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonPropertyOrder({"account", "description", "value", "currency", "category"})
public class Operation {
    private UUID account;
    private String description;
    private double value;
    private UUID currency;
    private UUID category;

    @JsonIgnore
    private String user;

    public UUID getAccount() {
        return account;
    }

    public void setAccount(UUID account) {
        this.account = account;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public UUID getCurrency() {
        return currency;
    }

    public void setCurrency(UUID currency) {
        this.currency = currency;
    }

    public UUID getCategory() {
        return category;
    }

    public void setCategory(UUID category) {
        this.category = category;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static class Builder {
        private final Operation operation;

        private Builder() {
            operation = new Operation();
        }

        public Builder setAccount(UUID account) {
            operation.account = account;
            return this;
        }

        public Builder setDescription(String description) {
            operation.description = description;
            return this;
        }

        public Builder setValue(double value) {
            operation.value = value;
            return this;
        }

        public Builder setCurrency(UUID currency) {
            operation.currency = currency;
            return this;
        }

        public Builder setCategory(UUID category) {
            operation.category = category;
            return this;
        }

        public Builder setUser(String user) {
            operation.user = user;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public Operation build() {
            return operation;
        }
    }
}
