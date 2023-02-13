package by.itacademy.account.scheduler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonPropertyOrder({"uuid", "dt_create", "dt_update", "schedule", "operation"})
public class ScheduledOperation {
    @JsonProperty("uuid")
    private UUID id;

    private LocalDateTime dtCreate;
    private LocalDateTime dtUpdate;
    private Schedule schedule;
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
        private final ScheduledOperation scheduledOperation;

        private Builder() {
            scheduledOperation = new ScheduledOperation();
        }

        public Builder setId(UUID id) {
            scheduledOperation.id = id;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            scheduledOperation.dtCreate = dtCreate;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            scheduledOperation.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setSchedule(Schedule schedule) {
            scheduledOperation.schedule = schedule;
            return this;
        }

        public Builder setOperation(Operation operation) {
            scheduledOperation.operation = operation;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public ScheduledOperation build() {
            return scheduledOperation;
        }
    }
}
