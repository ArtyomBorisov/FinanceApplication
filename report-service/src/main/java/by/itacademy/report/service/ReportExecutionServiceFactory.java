package by.itacademy.report.service;

import by.itacademy.report.constant.ReportType;

import java.time.LocalDateTime;

public interface ReportExecutionServiceFactory {
    ReportExecutionService getService(ReportType reportType);

    String getDescription(ReportType reportType, LocalDateTime ldt);
}
