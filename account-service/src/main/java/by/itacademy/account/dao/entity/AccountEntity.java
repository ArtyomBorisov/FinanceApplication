package by.itacademy.account.dao.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account", schema = "app", uniqueConstraints = @UniqueConstraint(columnNames = {"title", "user"}))
public class AccountEntity extends BaseEntity {

    @Column(nullable = false)
    private String title;

    private String description;

    @OneToOne
    @JoinColumn(name = "balance", nullable = false)
    private BalanceEntity balance;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private UUID currency;

    @Column(name = "\"user\"", nullable = false)
    private String user;

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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static class Builder {
        private final AccountEntity entity;

        private Builder() {
            entity = new AccountEntity();
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

        public Builder setBalance(BalanceEntity balance) {
            entity.balance = balance;
            return this;
        }

        public Builder setType(String type) {
            entity.type = type;
            return this;
        }

        public Builder setCurrency(UUID currency) {
            entity.currency = currency;
            return this;
        }

        public Builder setUser(String user) {
            entity.user = user;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public AccountEntity build() {
            return entity;
        }
    }
}
