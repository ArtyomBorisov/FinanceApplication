package by.itacademy.classifier.model;

import by.itacademy.classifier.model.api.serializer.CustomLocalDateTimeDeserializer;
import by.itacademy.classifier.model.api.serializer.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.UUID;

public class Category {
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

    public static class Builder {
        private Category category;

        private Builder() {
            this.category = new Category();
        }

        public Builder setId(UUID id) {
            this.category.id = id;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            this.category.dtCreate = dtCreate;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            this.category.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setTitle(String title) {
            this.category.title = title;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public Category build() {
            return this.category;
        }
    }
}
