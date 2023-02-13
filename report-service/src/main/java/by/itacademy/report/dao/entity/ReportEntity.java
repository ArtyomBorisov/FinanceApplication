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
            entity = new ReportEntity();
        }

        public Builder setId(UUID id) {
            entity.id = id;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            entity.dtCreate = dtCreate;
            return this;
        }

        public Builder setDtUpdate(LocalDateTime dtUpdate) {
            entity.dtUpdate = dtUpdate;
            return this;
        }

        public Builder setStatus(String status) {
            entity.status = status;
            return this;
        }

        public Builder setType(String type) {
            entity.type = type;
            return this;
        }

        public Builder setDescription(String description) {
            entity.description = description;
            return this;
        }

        public Builder setParams(Params params) {
            entity.params = params;
            return this;
        }

        public Builder setUser(String user) {
            entity.user = user;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public ReportEntity build() {
            return entity;
        }
    }
}
