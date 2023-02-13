package by.itacademy.report.dto;

import by.itacademy.report.constant.ReportType;
import by.itacademy.report.constant.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonPropertyOrder({"uuid", "dt_create", "dt_update", "status", "type", "description", "params"})
public class Report {
    @JsonProperty("uuid")
    private UUID id;

    private LocalDateTime dtCreate;
    private LocalDateTime dtUpdate;
    private Status status;
    private ReportType type;
    private String description;
    private Params params;

    @JsonIgnore
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static class Builder {
        private final Report report;

        private Builder() {
            report = new Report();
        }

        public Builder setId(UUID id) {
            report.id = id;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            report.dtCreate = dtCreate;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            report.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setStatus(Status status) {
            report.status = status;
            return this;
        }

        public Builder setType(ReportType type) {
            report.type = type;
            return this;
        }

        public Builder setDescription(String description) {
            report.description = description;
            return this;
        }

        public Builder setParams(Params params) {
            report.params = params;
            return this;
        }

        public Builder setUser(String user) {
            report.user = user;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public Report build() {
            return report;
        }
    }
}
