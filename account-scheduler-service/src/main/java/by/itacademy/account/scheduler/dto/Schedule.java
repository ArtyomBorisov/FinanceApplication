package by.itacademy.account.scheduler.dto;

import by.itacademy.account.scheduler.constant.TimeUnit;
import by.itacademy.account.scheduler.utils.serializer.CustomLocalDateTimeDeserializer;
import by.itacademy.account.scheduler.utils.serializer.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

public class Schedule {
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonProperty("stop_time")
    private LocalDateTime stopTime;

    private long interval;

    @JsonProperty("time_unit")
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
            this.schedule = new Schedule();
        }

        public Builder setStartTime(LocalDateTime startTime) {
            this.schedule.startTime = startTime;
            return this;
        }

        public Builder setStopTime(LocalDateTime stopTime) {
            this.schedule.stopTime = stopTime;
            return this;
        }

        public Builder setInterval(long interval) {
            this.schedule.interval = interval;
            return this;
        }

        public Builder setTimeUnit(TimeUnit timeUnit) {
            this.schedule.timeUnit = timeUnit;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public Schedule build() {
            return this.schedule;
        }
    }
}
