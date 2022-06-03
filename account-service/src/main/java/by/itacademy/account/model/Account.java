package by.itacademy.account.model;

import by.itacademy.account.model.api.Type;
import by.itacademy.account.model.api.serializer.CustomLocalDateTimeDeserializer;
import by.itacademy.account.model.api.serializer.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.UUID;

public class Account {
    @JsonProperty("uuid")
    private UUID id;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonProperty("dt_create")
    private LocalDateTime dtCreate;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonProperty("dt_update")
    private LocalDateTime dtUpdate;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("balance")
    private double balance;

    @JsonProperty("type")
    private Type type;

    @JsonProperty("currency")
    private UUID currency;

    @JsonIgnore
    private String user;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getDtCreate() {
        return dtCreate;
    }

    public void setDtCreate(LocalDateTime dtCreate) {
        this.dtCreate = dtCreate;
    }

    public LocalDateTime getDtUpdate() {
        return dtUpdate;
    }

    public void setDtUpdate(LocalDateTime dtUpdate) {
        this.dtUpdate = dtUpdate;
    }

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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
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
        private Account account;

        private Builder() {
            this.account = new Account();
        }

        public Builder setId(UUID id) {
            this.account.id = id;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            this.account.dtCreate = dtCreate;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            this.account.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setTitle(String title) {
            this.account.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.account.description = description;
            return this;
        }

        public Builder setBalance(double balance) {
            this.account.balance = balance;
            return this;
        }

        public Builder setType(Type type) {
            this.account.type = type;
            return this;
        }

        public Builder setCurrency(UUID currency) {
            this.account.currency = currency;
            return this;
        }

        public Builder setUser(String user) {
            this.account.user = user;
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
