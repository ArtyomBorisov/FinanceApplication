package by.itacademy.classifier.dao.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "currency", schema = "app")
public class CurrencyEntity extends BaseEntity {

    @Column(nullable = false, unique = true, updatable = false)
    private String title;

    @Column(nullable = false)
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
        private final CurrencyEntity entity;

        private Builder() {
            entity = new CurrencyEntity();
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

        public Builder setDescription(String description) {
            entity.description = description;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public CurrencyEntity build() {
            return entity;
        }
    }
}
