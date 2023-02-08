package by.itacademy.classifier.dao.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "currency", schema = "app")
public class CurrencyEntity {
    @Id
    private UUID id;

    @Column(name = "dt_create", nullable = false, updatable = false)
    private LocalDateTime dtCreate;

    @Version
    @Column(name = "dt_update", nullable = false)
    private LocalDateTime dtUpdate;

    @Column(nullable = false, unique = true, updatable = false)
    private String title;

    @Column(nullable = false)
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
        private final CurrencyEntity currencyEntity;

        private Builder() {
            this.currencyEntity = new CurrencyEntity();
        }

        public Builder setId(UUID id) {
            this.currencyEntity.id = id;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            this.currencyEntity.dtCreate = dtCreate;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            this.currencyEntity.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setTitle(String title) {
            this.currencyEntity.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.currencyEntity.description = description;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public CurrencyEntity build() {
            return this.currencyEntity;
        }
    }
}
