package by.itacademy.mail.scheduler.dto;

import by.itacademy.mail.scheduler.constant.ReportType;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class MonthlyReport {
    @Email(regexp = ".+@.+\\..+")
    @NotBlank
    private String email;

    @JsonProperty("report_type")
    private ReportType reportType;

    public MonthlyReport() {
    }

    public MonthlyReport(String email, ReportType reportType) {
        this.email = email;
        this.reportType = reportType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
}
