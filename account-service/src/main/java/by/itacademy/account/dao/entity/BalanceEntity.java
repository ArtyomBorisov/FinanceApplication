package by.itacademy.account.dao.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "balance", schema = "app")
public class BalanceEntity {
    @Id
    private UUID id;

    @Version
    @Column(name = "dt_update", nullable = false)
    private LocalDateTime dtUpdate;

    private double sum;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getDtUpdate() {
        return dtUpdate;
    }

    public void setDtUpdate(LocalDateTime dtUpdate) {
        this.dtUpdate = dtUpdate;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public static class Builder {
        private final BalanceEntity entity;

        private Builder() {
            entity = new BalanceEntity();
        }

        public Builder setId(UUID id) {
            entity.id = id;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            entity.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setSum(double sum) {
            entity.sum = sum;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public BalanceEntity build() {
            return this.entity;
        }
    }
}
