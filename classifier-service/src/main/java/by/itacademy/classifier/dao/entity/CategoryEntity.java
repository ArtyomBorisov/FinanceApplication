package by.itacademy.classifier.dao.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "category", schema = "app")
public class CategoryEntity extends BaseEntity {

    @Column(nullable = false, unique = true, updatable = false)
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static class Builder {
        private final CategoryEntity entity;

        private Builder() {
            entity = new CategoryEntity();
        }

        public Builder setId(UUID id) {
            entity.setId(id);
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            entity.setDtCreate(dtCreate);
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            entity.setDtUpdate(dtUpdate);
            return this;
        }

        public Builder setTitle(String title) {
            entity.title = title;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public CategoryEntity build() {
            return entity;
        }
    }
}
