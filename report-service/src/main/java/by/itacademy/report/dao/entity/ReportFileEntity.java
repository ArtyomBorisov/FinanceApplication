package by.itacademy.report.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "report_file", schema = "app")
public class ReportFileEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "data")
    private byte[] data;

    @Column(name = "\"user\"", nullable = false)
    private String user;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static class Builder {
        private final ReportFileEntity entity;

        private Builder() {
            entity = new ReportFileEntity();
        }

        public Builder setId(UUID id) {
            entity.id = id;
            return this;
        }

        public Builder setData(byte[] data) {
            entity.data = data;
            return this;
        }

        public Builder setUser(String user) {
            entity.user = user;
            return this;
        }

        public static Builder createBuilder() {
            return new Builder();
        }

        public ReportFileEntity build() {
            return entity;
        }
    }
}
