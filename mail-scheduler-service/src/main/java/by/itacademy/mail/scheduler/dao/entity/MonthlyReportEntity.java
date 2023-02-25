package by.itacademy.mail.scheduler.dao.entity;

import by.itacademy.mail.scheduler.constant.ReportType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_report", schema = "app")
@TypeDef(name = "report_type_enum", typeClass = PostgreSQLEnumType.class)
public class MonthlyReportEntity {
    @Id
    private String login;

    @Column(name = "dt_create", nullable = false, updatable = false)
    private LocalDateTime dtCreate;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    @Type(type = "report_type_enum")
    private ReportType reportType;

    @Column(nullable = false)
    private String email;

    @Column(name = "last_report")
    private String lastReport;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public LocalDateTime getDtCreate() {
        return dtCreate;
    }

    public void setDtCreate(LocalDateTime dtCreate) {
        this.dtCreate = dtCreate;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastReport() {
        return lastReport;
    }

    public void setLastReport(String lastReport) {
        this.lastReport = lastReport;
    }

    public static class Builder {
        private final MonthlyReportEntity entity;

        private Builder() {
            entity = new MonthlyReportEntity();
        }

        public Builder setLogin(String login) {
            entity.login = login;
            return this;
        }

        public Builder setDtCreate(LocalDateTime dtCreate) {
            entity.dtCreate = dtCreate;
            return this;
        }

        public Builder setReportType(ReportType reportType) {
            entity.reportType = reportType;
            return this;
        }

        public Builder setEmail(String email) {
            entity.email = email;
            return this;
        }

        public Builder setLastReport(String lastReport) {
            entity.lastReport = lastReport;
            return this;
        }

        public MonthlyReportEntity build() {
            return entity;
        }

        public static Builder createBuilder() {
            return new Builder();
        }
    }
}
