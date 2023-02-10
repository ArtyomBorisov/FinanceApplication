package by.itacademy.report.dao.entity;

import by.itacademy.report.converter.ParamsToStringConverter;
import by.itacademy.report.dto.Params;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report", schema = "app")
public class ReportEntity {
    @Id
    private UUID id;

    @Column(name = "dt_create")
    private LocalDateTime dtCreate;

    @Version
    @Column(name = "dt_update")
    private LocalDateTime dtUpdate;

    private String status;
    private String type;
    private String description;

    @Convert(converter = ParamsToStringConverter.class)
    private Params params;

    @Column(name = "\"user\"", nullable = false)
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
        private final ReportEntity entity;

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

        public Builder setParams(Params params) {
            this.entity.params = params;
            return this;
        }

        public Builder setUser(String user) {
            this.entity.user = user;
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
