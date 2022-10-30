package by.itacademy.classifier.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "category", schema = "app")
public class CategoryEntity {
    @Id
    private UUID id;

    @Column(name = "dt_create", nullable = false, updatable = false)
    private LocalDateTime dtCreate;

    @Version
    @Column(name = "dt_update", nullable = false)
    private LocalDateTime dtUpdate;

    @Column(nullable = false, unique = true, updatable = false)
    private String title;

    public UUID getId() {
        return id;
    }

    public LocalDateTime getDtCreate() {
        return dtCreate;
    }

    public LocalDateTime getDtUpdate() {
        return dtUpdate;
    }

    public String getTitle() {
        return title;
    }

    public static class Builder {
        private CategoryEntity categoryEntity;

        private Builder() {
            this.categoryEntity = new CategoryEntity();
        }

        public Builder setId(UUID id) {
            this.categoryEntity.id = id;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            this.categoryEntity.dtCreate = dtCreate;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            this.categoryEntity.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setTitle(String title) {
            this.categoryEntity.title = title;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public CategoryEntity build() {
            return this.categoryEntity;
        }
    }
}
