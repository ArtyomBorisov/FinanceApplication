package by.itacademy.account.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "balance", schema = "app")
public class BalanceEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Version
    @Column(name = "dt_update", nullable = false)
    private LocalDateTime dtUpdate;

    @Column(name = "sum")
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
        private BalanceEntity entity;

        private Builder() {
            this.entity = new BalanceEntity();
        }

        public Builder setId(UUID id) {
            this.entity.id = id;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            this.entity.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setSum(double sum) {
            this.entity.sum = sum;
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
