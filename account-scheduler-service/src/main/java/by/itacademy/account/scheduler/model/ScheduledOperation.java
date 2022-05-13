package by.itacademy.account.scheduler.model;

import by.itacademy.account.scheduler.model.api.serializer.CustomLocalDateTimeDeserializer;
import by.itacademy.account.scheduler.model.api.serializer.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.UUID;

public class ScheduledOperation {
    @JsonProperty("uuid")
    private UUID id;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonProperty("dt_create")
    private LocalDateTime dtCreate;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonProperty("dt_update")
    private LocalDateTime dtUpdate;

    @JsonProperty("schedule")
    private Schedule schedule;

    @JsonProperty("operation")
    private Operation operation;

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

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public static class Builder {
        private ScheduledOperation scheduledOperation;

        private Builder() {
            this.scheduledOperation = new ScheduledOperation();
        }

        public Builder setId(UUID id) {
            this.scheduledOperation.id = id;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            this.scheduledOperation.dtCreate = dtCreate;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            this.scheduledOperation.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setSchedule(Schedule schedule) {
            this.scheduledOperation.schedule = schedule;
            return this;
        }

        public Builder setOperation(Operation operation) {
            this.scheduledOperation.operation = operation;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public ScheduledOperation build() {
            return this.scheduledOperation;
        }
    }
}
