package by.itacademy.classifier.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonPropertyOrder({"uuid", "dt_create", "dt_update", "title"})
public class Category extends BaseDto {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static class Builder {
        private final Category category;

        private Builder() {
            category = new Category();
        }

        public Builder setId(UUID id) {
            category.setId(id);
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            category.setDtCreate(dtCreate);
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            category.setDtUpdate(dtUpdate);
            return this;
        }

        public Builder setTitle(String title) {
            category.title = title;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public Category build() {
            return category;
        }
    }
}
