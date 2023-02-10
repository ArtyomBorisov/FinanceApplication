package by.itacademy.report.dto;

import by.itacademy.report.constant.ReportType;
import by.itacademy.report.constant.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public class Report {
    @JsonProperty("uuid")
    private UUID id;

    @JsonProperty("dt_create")
    private LocalDateTime dtCreate;

    @JsonProperty("dt_update")
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
            this.report = new Report();
        }

        public Builder setId(UUID id) {
            this.report.id = id;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            this.report.dtCreate = dtCreate;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            this.report.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setStatus(Status status) {
            this.report.status = status;
            return this;
        }

        public Builder setType(ReportType type) {
            this.report.type = type;
            return this;
        }

        public Builder setDescription(String description) {
            this.report.description = description;
            return this;
        }

        public Builder setParams(Params params) {
            this.report.params = params;
            return this;
        }

        public Builder setUser(String user) {
            this.report.user = user;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public Report build() {
            return this.report;
        }
    }
}
