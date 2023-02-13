package by.itacademy.account.scheduler.dao.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scheduled_operation", schema = "app")
public class ScheduledOperationEntity {
    @Id
    private UUID id;

    @Column(name = "dt_create", nullable = false)
    private LocalDateTime dtCreate;

    @Version
    @Column(name = "dt_update", nullable = false)
    private LocalDateTime dtUpdate;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "stop_time")
    private LocalDateTime stopTime;

    private Long interval;

    @Column(name = "time_unit")
    private String timeUnit;

    @Column(nullable = false)
    private UUID account;
    private String description;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private UUID currency;

    @Column(nullable = false)
    private UUID category;

    @Column(name = "\"user\"")
    private String user;

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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public void setStopTime(LocalDateTime stopTime) {
        this.stopTime = stopTime;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public UUID getAccount() {
        return account;
    }

    public void setAccount(UUID account) {
        this.account = account;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public UUID getCategory() {
        return category;
    }

    public void setCategory(UUID category) {
        this.category = category;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static class Builder {
        private final ScheduledOperationEntity scheduledOperationEntity;

        private Builder() {
            scheduledOperationEntity = new ScheduledOperationEntity();
        }

        public Builder setId(UUID id) {
            scheduledOperationEntity.id = id;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            scheduledOperationEntity.dtCreate = dtCreate;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            scheduledOperationEntity.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setStartTime(LocalDateTime startTime) {
            scheduledOperationEntity.startTime = startTime;
            return this;
        }

        public Builder setStopTime(LocalDateTime stopTime) {
            scheduledOperationEntity.stopTime = stopTime;
            return this;
        }

        public Builder setInterval(long interval) {
            scheduledOperationEntity.interval = interval;
            return this;
        }

        public Builder setTimeUnit(String timeUnit) {
            scheduledOperationEntity.timeUnit = timeUnit;
            return this;
        }

        public Builder setAccount(UUID account) {
            scheduledOperationEntity.account = account;
            return this;
        }

        public Builder setDescription(String description) {
            scheduledOperationEntity.description = description;
            return this;
        }

        public Builder setValue(double value) {
            scheduledOperationEntity.value = value;
            return this;
        }

        public Builder setCurrency(UUID currency) {
            scheduledOperationEntity.currency = currency;
            return this;
        }

        public Builder setCategory(UUID category) {
            scheduledOperationEntity.category = category;
            return this;
        }

        public Builder setUser(String user) {
            scheduledOperationEntity.user = user;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public ScheduledOperationEntity build() {
            return scheduledOperationEntity;
        }
    }
}
