package by.itacademy.classifier.dto;

import by.itacademy.classifier.utils.serializer.CustomLocalDateTimeDeserializer;
import by.itacademy.classifier.utils.serializer.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.UUID;

public class Currency {
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

    private String title;
    private String description;

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

    public static class Builder {
        private final Currency currency;

        private Builder() {
            this.currency = new Currency();
        }

        public Builder setId(UUID id) {
            this.currency.id = id;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            this.currency.dtCreate = dtCreate;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            this.currency.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setTitle(String title) {
            this.currency.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.currency.description = description;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public Currency build() {
            return this.currency;
        }

    }
}
