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

    private long interval;

    @Column(name = "time_unit")
    private String timeUnit;

    private UUID account;
    private String description;
    private double value;
    private UUID currency;
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

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
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
        private ScheduledOperationEntity scheduledOperationEntity;

        private Builder() {
            this.scheduledOperationEntity = new ScheduledOperationEntity();
        }

        public Builder setId(UUID id) {
            this.scheduledOperationEntity.id = id;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            this.scheduledOperationEntity.dtCreate = dtCreate;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            this.scheduledOperationEntity.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setStartTime(LocalDateTime startTime) {
            this.scheduledOperationEntity.startTime = startTime;
            return this;
        }

        public Builder setStopTime(LocalDateTime stopTime) {
            this.scheduledOperationEntity.stopTime = stopTime;
            return this;
        }

        public Builder setInterval(long interval) {
            this.scheduledOperationEntity.interval = interval;
            return this;
        }

        public Builder setTimeUnit(String timeUnit) {
            this.scheduledOperationEntity.timeUnit = timeUnit;
            return this;
        }

        public Builder setAccount(UUID account) {
            this.scheduledOperationEntity.account = account;
            return this;
        }

        public Builder setDescription(String description) {
            this.scheduledOperationEntity.description = description;
            return this;
        }

        public Builder setValue(double value) {
            this.scheduledOperationEntity.value = value;
            return this;
        }

        public Builder setCurrency(UUID currency) {
            this.scheduledOperationEntity.currency = currency;
            return this;
        }

        public Builder setCategory(UUID category) {
            this.scheduledOperationEntity.category = category;
            return this;
        }

        public Builder setUser(String user) {
            this.scheduledOperationEntity.user = user;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public ScheduledOperationEntity build() {
            return this.scheduledOperationEntity;
        }
    }
}
