package by.itacademy.classifier.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonPropertyOrder({"uuid", "dt_create", "dt_update", "title", "description"})
public class Currency extends BaseDto {

    private String title;
    private String description;

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
            currency = new Currency();
        }

        public Builder setId(UUID id) {
            currency.setId(id);
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            currency.setDtCreate(dtCreate);
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            currency.setDtUpdate(dtUpdate);
            return this;
        }

        public Builder setTitle(String title) {
            currency.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            currency.description = description;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public Currency build() {
            return currency;
        }

    }
}
