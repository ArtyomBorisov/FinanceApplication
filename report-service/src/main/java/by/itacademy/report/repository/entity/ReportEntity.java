package by.itacademy.report.repository.entity;

import by.itacademy.report.repository.api.converter.MapToStringConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "report", schema = "app")
public class ReportEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "dt_create")
    private LocalDateTime dtCreate;

    @Version
    @Column(name = "dt_update")
    private LocalDateTime dtUpdate;

    @Column(name = "status")
    private String status;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

    @Convert(converter = MapToStringConverter.class)
    @Column(name = "params")
    private Map<String, Object> params;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public static class Builder {
        private ReportEntity entity;

        private Builder() {
            this.entity = new ReportEntity();
        }

        public Builder setId(UUID id) {
            this.entity.id = id;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            this.entity.dtCreate = dtCreate;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            this.entity.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setStatus(String status) {
            this.entity.status = status;
            return this;
        }

        public Builder setType(String type) {
            this.entity.type = type;
            return this;
        }

        public Builder setDescription(String description) {
            this.entity.description = description;
            return this;
        }

        public Builder setParams(Map<String, Object> params) {
            this.entity.params = params;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public ReportEntity build() {
            return this.entity;
        }
    }
}
