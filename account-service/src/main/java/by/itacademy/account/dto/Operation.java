package by.itacademy.account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonPropertyOrder({"uuid", "dt_create", "dt_update", "date", "description", "category", "value", "currency"})
public class Operation extends BaseDto {

    private LocalDateTime date;
    private String description;
    private UUID category;
    private double value;
    private UUID currency;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Account account;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getCategory() {
        return category;
    }

    public void setCategory(UUID category) {
        this.category = category;
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

    public Account getAccount() {
        return account;
    }

    public Operation setAccount(Account account) {
        this.account = account;
        return this;
    }

    public static class Builder {
        private final Operation operation;

        private Builder() {
            operation = new Operation();
        }

        public Builder setId(UUID id) {
            operation.setId(id);
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            operation.setDtCreate(dtCreate);
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            operation.setDtUpdate(dtUpdate);
            return this;
        }

        public Builder setDate(LocalDateTime date) {
            operation.date = date;
            return this;
        }

        public Builder setDescription(String description) {
            operation.description = description;
            return this;
        }

        public Builder setCategory(UUID category) {
            operation.category = category;
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

        public Builder setAccount(Account account) {
            operation.account = account;
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
