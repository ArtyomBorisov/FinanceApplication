package by.itacademy.classifier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public class Category {
    @JsonProperty("uuid")
    private UUID id;

    @JsonProperty("dt_create")
    private LocalDateTime dtCreate;

    @JsonProperty("dt_update")
    private LocalDateTime dtUpdate;

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
        private final Category category;

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
