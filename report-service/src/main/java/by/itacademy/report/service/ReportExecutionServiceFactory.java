package by.itacademy.report.service;

import by.itacademy.report.constant.ReportType;

public interface ReportExecutionServiceFactory {
    ReportDataExecutionService getService(ReportType reportType);
}
