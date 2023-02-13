package by.itacademy.account.scheduler.dto;

import by.itacademy.account.scheduler.constant.TimeUnit;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonPropertyOrder({"start_time", "stop_time", "interval", "time_unit"})
public class Schedule {
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private long interval;
    private TimeUnit timeUnit;

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

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public static class Builder {
        private final Schedule schedule;

        private Builder() {
            schedule = new Schedule();
        }

        public Builder setStartTime(LocalDateTime startTime) {
            schedule.startTime = startTime;
            return this;
        }

        public Builder setStopTime(LocalDateTime stopTime) {
            schedule.stopTime = stopTime;
            return this;
        }

        public Builder setInterval(long interval) {
            schedule.interval = interval;
            return this;
        }

        public Builder setTimeUnit(TimeUnit timeUnit) {
            schedule.timeUnit = timeUnit;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public Schedule build() {
            return schedule;
        }
    }
}
