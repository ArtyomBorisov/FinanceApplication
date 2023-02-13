package by.itacademy.account.dao.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "operation", schema = "app")
public class OperationEntity extends BaseEntity {

    @Column(nullable = false)
    private LocalDateTime date;

    private String description;

    @Column(nullable = false)
    private UUID category;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private UUID currency;

    @ManyToOne
    @JoinColumn(name = "account", nullable = false, updatable = false)
    private AccountEntity accountEntity;

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

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
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
        private final OperationEntity entity;

        private Builder() {
            entity = new OperationEntity();
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

        public Builder setDate(LocalDateTime date) {
            entity.date = date;
            return this;
        }

        public Builder setDescription(String description) {
            entity.description = description;
            return this;
        }

        public Builder setCategory(UUID category) {
            entity.category = category;
            return this;
        }

        public Builder setValue(double value) {
            entity.value = value;
            return this;
        }

        public Builder setCurrency(UUID currency) {
            entity.currency = currency;
            return this;
        }

        public Builder setAccountEntity(AccountEntity accountEntity) {
            entity.accountEntity = accountEntity;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public OperationEntity build() {
            return entity;
        }
    }
}
