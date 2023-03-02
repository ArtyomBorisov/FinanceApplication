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

    private BalanceEntity() {
    }

    private BalanceEntity(UUID id, LocalDateTime dtUpdate, double sum) {
        this.id = id;
        this.dtUpdate = dtUpdate;
        this.sum = sum;
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getDtUpdate() {
        return dtUpdate;
    }

    public double getSum() {
        return sum;
    }

    public static BalanceEntity createDefaultBalance(UUID id, LocalDateTime dtUpdate) {
        return new BalanceEntity(id, dtUpdate, 0);
    }
}
