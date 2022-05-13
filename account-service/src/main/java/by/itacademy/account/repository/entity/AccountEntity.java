package by.itacademy.account.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account", schema = "app")
public class AccountEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "dt_create", nullable = false, updatable = false)
    private LocalDateTime dtCreate;

    @Version
    @Column(name = "dt_update", nullable = false)
    private LocalDateTime dtUpdate;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "balance", nullable = false)
    private BalanceEntity balance;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "currency", nullable = false)
    private UUID currency;

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

    public BalanceEntity getBalance() {
        return balance;
    }

    public void setBalance(BalanceEntity balance) {
        this.balance = balance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getCurrency() {
        return currency;
    }

    public void setCurrency(UUID currency) {
        this.currency = currency;
    }

    public static class Builder {
        private AccountEntity entity;

        private Builder() {
            this.entity = new AccountEntity();
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

        public Builder setTitle(String title) {
            this.entity.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.entity.description = description;
            return this;
        }

        public Builder setBalance(BalanceEntity balance) {
            this.entity.balance = balance;
            return this;
        }

        public Builder setType(String type) {
            this.entity.type = type;
            return this;
        }

        public Builder setCurrency(UUID currency) {
            this.entity.currency = currency;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public AccountEntity build() {
            return this.entity;
        }
    }
}
