package by.itacademy.account.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "operation", schema = "app")
public class OperationEntity {
    @Id
    @Column(name = "id", updatable = false)
    private UUID id;

    @Column(name = "dt_create", nullable = false, updatable = false)
    private LocalDateTime dtCreate;

    @Version
    @Column(name = "dt_update", nullable = false)
    private LocalDateTime dtUpdate;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "description")
    private String description;

    @Column(name = "category", nullable = false)
    private UUID category;

    @Column(name = "value", nullable = false)
    private double value;

    @Column(name = "currency", nullable = false)
    private UUID currency;

    @ManyToOne
    @JoinColumn(name = "account", nullable = false, updatable = false)
    private AccountEntity accountEntity;

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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getCategory() {
        return category;
    }

    public void setCategory(UUID category) {
        this.category = category;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public UUID getCurrency() {
        return currency;
    }

    public void setCurrency(UUID currency) {
        this.currency = currency;
    }

    public AccountEntity getAccountEntity() {
        return accountEntity;
    }

    public void setAccountEntity(AccountEntity accountEntity) {
        this.accountEntity = accountEntity;
    }

    public static class Builder {
        private OperationEntity entity;

        private Builder() {
            this.entity = new OperationEntity();
        }

        public Builder setId(UUID id) {
            this.entity.id = id;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            this.entity.dtCreate = dtCreate;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            this.entity.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setDate(LocalDateTime date) {
            this.entity.date = date;
            return this;
        }

        public Builder setDescription(String description) {
            this.entity.description = description;
            return this;
        }

        public Builder setCategory(UUID category) {
            this.entity.category = category;
            return this;
        }

        public Builder setValue(double value) {
            this.entity.value = value;
            return this;
        }

        public Builder setCurrency(UUID currency) {
            this.entity.currency = currency;
            return this;
        }

        public Builder setAccountEntity(AccountEntity accountEntity) {
            this.entity.accountEntity = accountEntity;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public OperationEntity build() {
            return this.entity;
        }
    }
}
